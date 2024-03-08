package fi.metatavu.edelphi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fi.metatavu.edelphi.keycloak.KeycloakException;
import fi.metatavu.edelphi.keycloak.UsersApi;
import fi.metatavu.edelphi.keycloak.invoker.ApiClient;
import fi.metatavu.edelphi.keycloak.invoker.ApiException;
import fi.metatavu.edelphi.keycloak.model.CredentialRepresentation;
import fi.metatavu.edelphi.keycloak.model.UserRepresentation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.edelphi.auth.OAuthAccessToken;
import fi.metatavu.edelphi.dao.base.AuthSourceDAO;
import fi.metatavu.edelphi.dao.base.AuthSourceSettingDAO;
import fi.metatavu.edelphi.dao.users.UserIdentificationDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.base.AuthSourceSetting;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification;

/**
 * Utilities for Keycloak
 * 
 * @author Antti Leppä
 */
public class KeycloakUtils {

  public static final String KEYCLOAK_AUTH_SOURCE = "Keycloak";

  private static final Logger logger = LoggerFactory.getLogger(RequestUtils.class);
  
  /**
   * Creates user into Keycloak (if missing)
   * 
   * @param user user
   * @param password password
   * @param passwordTemporary whether password is temporary or not
   * @param emailVerified whether email is verified
   */
  public static void createUser(User user, String password, boolean passwordTemporary, boolean emailVerified) throws KeycloakException {
    UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
    
    Map<String, String> settings = getKeycloakSettings();

    UsersApi usersApi = getUsersApi(settings);
    String email = user.getDefaultEmailAsString();
    String realm = getRealm(settings);

    UserRepresentation userRepresentation = findUser(usersApi, realm, email);
    if (userRepresentation == null) {
      userRepresentation = createUser(usersApi, realm, user, email, password, passwordTemporary, emailVerified);
    }

    if (userRepresentation == null || userRepresentation.getId() == null) {
      logger.error("Failed to create user for {}", email);
      return;
    }
    
    AuthSource keycloakAuthSource = getKeycloakAuthSource();
    
    UUID keycloakUserId = UUID.fromString(userRepresentation.getId());
    
    UUID storedKeycloakId = getUserKeycloakId(user);
    if (storedKeycloakId == null) {
      userIdentificationDAO.create(user, keycloakUserId.toString(), keycloakAuthSource);
    } else if (!storedKeycloakId.equals(keycloakUserId)) {
      userIdentificationDAO.listByUserAndAuthSource(user, keycloakAuthSource).stream().forEach(userIdentificationDAO::delete);
      userIdentificationDAO.create(user, keycloakUserId.toString(), keycloakAuthSource);
    }
  }
  
  /**
   * Returns access token for impersonated user
   * 
   * @param user user
   * @return access token for impersonated user
   */
  public static OAuthAccessToken getImpersonatedToken(User user) {
    UUID userId = getUserKeycloakId(user);
    if (userId != null) {
      return getImpersonatedToken(userId);
    }
    
    return null;
  }
  
  /**
   * Returns access token for impersonated user
   * 
   * @param userId user
   * @return access token for impersonated user
   */
  public static OAuthAccessToken getImpersonatedToken(UUID userId) {
    Map<String, String> settings = getKeycloakSettings();
    String serverUrl = getServerUrl(settings);
    String realm = getRealm(settings);
    String impersonateClientId = getImpersonateClientId(settings);
    String impersonateClientSecret = getImpersonateClientSecret(settings);
    String impersonatePassword = getImpersonatePassword(settings);
    String impersonateUser = getImpersonateUser(settings);
    String impersonateRedirectUrl = getImpersonateRedirectUrl(settings);
    
    String adminAccessToken = getAccessToken(serverUrl, realm, impersonateClientId, impersonateClientSecret, impersonateUser, impersonatePassword);
    if (adminAccessToken == null) {
      return null;
    }
    
    List<Cookie> cookies = getImpersonationCookies(serverUrl, realm, adminAccessToken, userId);
    return exchangeImpersonationCookiesToToken(serverUrl, realm, impersonateClientId, userId, impersonateRedirectUrl, cookies);
  }

  /**
   * Creates user into Keycloak
   *
   * @param usersApi instance of UsersApi
   * @param realm realm
   * @param user user
   * @param email email
   * @param password password
   * @param passwordTemporary whether password is temporary or not
   * @param emailVerified whether email is verified
   * @throws KeycloakException thrown when Keycloak related error occurs
   */
  private static fi.metatavu.edelphi.keycloak.model.UserRepresentation createUser(UsersApi usersApi, String realm, User user, String email, String password, boolean passwordTemporary, boolean emailVerified) throws KeycloakException {
    fi.metatavu.edelphi.keycloak.model.CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
    credentialRepresentation.setType("password");
    credentialRepresentation.setValue(password);
    credentialRepresentation.setTemporary(passwordTemporary);

    fi.metatavu.edelphi.keycloak.model.UserRepresentation userRepresentation = new fi.metatavu.edelphi.keycloak.model.UserRepresentation();
    userRepresentation.setUsername(email);
    userRepresentation.setFirstName(user.getFirstName());
    userRepresentation.setLastName(user.getLastName());
    userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
    userRepresentation.setEnabled(true);
    userRepresentation.setEmail(email);
    userRepresentation.setEmailVerified(emailVerified);

    try {
      usersApi.adminRealmsRealmUsersPost(realm, userRepresentation);
    } catch (ApiException e) {
      throw new KeycloakException(e);
    }

    return findUser(usersApi, realm, email);
  }

  /**
   * Tries to find user by email
   *
   * @param usersApi instance of UsersApi
   * @param realm realm
   * @param email email
   * @return found user or null if not found
   */
  private static fi.metatavu.edelphi.keycloak.model.UserRepresentation findUser(UsersApi usersApi, String realm, String email) throws KeycloakException {
    Boolean briefRepresentation = false;
    Boolean emailVerified = null;
    Boolean enabled = true;
    Boolean exact = false;
    Integer first = 0;
    String firstName = null;
    String idpAlias = null;
    String idpUserId = null;
    String lastName = null;
    Integer max = 1;
    String q = null;
    String search = null;
    String username = null;

    try {
      List<fi.metatavu.edelphi.keycloak.model.UserRepresentation> users = usersApi.adminRealmsRealmUsersGet(
              realm,
              briefRepresentation,
              email,
              emailVerified,
              enabled,
              exact,
              first,
              firstName,
              idpAlias,
              idpUserId,
              lastName,
              max,
              q,
              search,
              username
      );

      if (users.isEmpty()) {
        return null;
      }

      return users.get(0);
    } catch (ApiException e) {
      throw new KeycloakException(e);
    }
  }
  
  /**
   * Returns session cookies for impersonated user
   * 
   * @param serverUrl server URL
   * @param realm realm
   * @param token impersonator user token
   * @param userId user id
   * @return session cookies
   */
  private static List<Cookie> getImpersonationCookies(String serverUrl, String realm, String token, UUID userId) {
    String uri = String.format("%s/admin/realms/%s/users/%s/impersonation", serverUrl, realm, userId);
    CookieStore cookieStore = new BasicCookieStore();
    
    try (CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build()) {
      HttpPost httpPost = new HttpPost(uri);
      httpPost.addHeader("Authorization", String.format("Bearer %s", token));
      
      try (CloseableHttpResponse response = client.execute(httpPost)) {
        return cookieStore.getCookies();
      }
    } catch (IOException e) {
      logger.debug("Failed to retrieve access token", e);
    }
    
    return null;    
  }
  
  /**
   * Exchanges cookies to access token
   * 
   * @param serverUrl server URL
   * @param realm realm
   * @param clientId client id
   * @param userId user id
   * @param callbackUrl callback URL
   * @param cookies cookies
   * @return access token
   */
  private static OAuthAccessToken exchangeImpersonationCookiesToToken(String serverUrl, String realm, String clientId, UUID userId, String callbackUrl, List<Cookie> cookies) {
    String uri = String.format("%s/realms/%s/protocol/openid-connect/auth", serverUrl, realm);
    
    CookieStore cookieStore = new BasicCookieStore();
    cookies.stream().forEach(cookieStore::addCookie);
    
    try (CloseableHttpClient client = HttpClients.custom().disableRedirectHandling().setDefaultCookieStore(cookieStore).build()) {
      URIBuilder uriBuilder = new URIBuilder(uri);
      uriBuilder.addParameter("response_mode", "fragment");
      uriBuilder.addParameter("response_type", "token");
      uriBuilder.addParameter("client_id", clientId);
      uriBuilder.addParameter("redirect_uri", callbackUrl);

      HttpGet httpGet = new HttpGet(uriBuilder.build());
      
      try (CloseableHttpResponse response = client.execute(httpGet)) {
        Header locationHeader = response.getLastHeader("Location");
        if (locationHeader == null) {
          return null;
        }
        
        Map<String, String> params = Arrays.stream(StringUtils.split(StringUtils.substringAfter(locationHeader.getValue(), "#"), "&"))
          .map(param -> StringUtils.split(param, "=", 2))
          .filter(split -> split.length == 2)
          .collect(Collectors.toMap(split -> split[0], split -> split[1]));
        
        Date expiresAt = getExpiresAt(NumberUtils.createInteger(params.get("expires_in")));
        
        return new OAuthAccessToken(userId.toString(), params.get("access_token"), "__IMPERSONATED__", expiresAt, null);
      }
      
    } catch (IOException | URISyntaxException e) {
      logger.debug("Failed to retrieve access token", e);
    }
    
    return null;
  }

  /**
   * Returns expires at from expires in
   * 
   * @param expiresIn expires in
   * @return expires at
   */
  private static Date getExpiresAt(int expiresIn) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(new Date());
    calendar.add(Calendar.SECOND, expiresIn);
    return calendar.getTime();
  }
  
  /**
   * Returns Keycloak id for an user
   * 
   * @param user user
   * @return Keycloak id or null if id could not be resolved
   */
  public static UUID getUserKeycloakId(User user) {
    UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
    
    AuthSource authSource = getKeycloakAuthSource();
    List<UserIdentification> userIdentifications = userIdentificationDAO.listByUserAndAuthSource(user, authSource);
    if (userIdentifications.size() == 1) {
      return UUID.fromString(userIdentifications.get(0).getExternalId());
    }
    
    if (userIdentifications.size() > 1) {
      logger.warn("User {} has more than one identity", user.getId());
    }
    
    return null;
  }

  /**
   * Returns Keycloak API client
   *
   * @param settings settings
   * @return Keycloak API client
   */
  private static UsersApi getUsersApi(Map<String, String> settings) {
    return new UsersApi(getApiClient(settings));
  }

  /**
   * Returns Keycloak API client
   *
   * @param settings settings
   * @return Keycloak API client
   */
  private static ApiClient getApiClient(Map<String, String> settings) {
    ApiClient result = new ApiClient();
    String realm = getRealm(settings);
    String serverUrl = getServerUrl(settings);
    String clientId = getAdminClientId(settings);
    String clientSecret = getAdminClientSecret(settings);
    String adminUser = getAdminUser(settings);
    String adminPass = getAdminPassword(settings);
    String token = getAccessToken(serverUrl, realm, clientId, clientSecret, adminUser, adminPass);
    result.setBasePath(serverUrl);
    result.addDefaultHeader("Authorization", String.format("Bearer %s", token));
    return result;
  }
  
  /**
   * Returns Keycloak auth source
   * 
   * @return Keycloak auth source
   */
  private static AuthSource getKeycloakAuthSource() {
    AuthSourceDAO authSourceDAO = new AuthSourceDAO();
    AuthSource authSource = authSourceDAO.findByStrategy(KEYCLOAK_AUTH_SOURCE);
    
    if (authSource == null) {
      logger.error("Could not find Keycloak auth source");
    }
    
    return authSource;
  }
  

  /**
   * Resolves an access token for realm, client, username and password
   * 
   * @param realm realm
   * @param clientId clientId
   * @param username username
   * @param password password
   * @return an access token
   * @throws IOException thrown on communication failure
   */
  private static String getAccessToken(String serverUrl, String realm, String clientId, String clientSecret, String username, String password) {
    String uri = String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
    
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      HttpPost httpPost = new HttpPost(uri);
      List<NameValuePair> params = new ArrayList<>();
      params.add(new BasicNameValuePair("client_id", clientId));
      params.add(new BasicNameValuePair("grant_type", "password"));
      params.add(new BasicNameValuePair("username", username));
      params.add(new BasicNameValuePair("password", password));
      
      if (clientSecret != null) {
        params.add(new BasicNameValuePair("client_secret", clientSecret));
      }
      
      httpPost.setEntity(new UrlEncodedFormEntity(params));
      
      try (CloseableHttpResponse response = client.execute(httpPost)) {
        try (InputStream inputStream = response.getEntity().getContent()) {
          Map<String, Object> responseMap = readJsonMap(inputStream);
          return (String) responseMap.get("access_token");
        }
      }
    } catch (IOException e) {
      logger.debug("Failed to retrieve access token", e);
    }
    
    return null;
  }

  /**
   * Returns auth source settings as map
   * 
   * @return auth source settings as map
   */
  private static Map<String, String> getKeycloakSettings() {
    AuthSourceSettingDAO authSourceSettingDAO = new AuthSourceSettingDAO();
    List<AuthSourceSetting> authSourceSettings = authSourceSettingDAO.listByAuthSource(getKeycloakAuthSource());
    return authSourceSettings.stream().collect(Collectors.toMap(AuthSourceSetting::getKey, AuthSourceSetting::getValue));
  }
  
  /**
   * Reads JSON src into Map
   * 
   * @param src input
   * @return map
   * @throws IOException throws IOException when there is error when reading the input 
   */
  private static Map<String, Object> readJsonMap(InputStream src) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(src, new TypeReference<Map<String, Object>>() {});
  }
  
  /**
   * Finds a id from Keycloak create response 
   * 
   * @param response response object
   * @return id
   */
  private static UUID getCreateResponseId(javax.ws.rs.core.Response response) {
    if (response.getStatus() != 201) {
      try {
        if (logger.isErrorEnabled()) {
          logger.error("Failed to execute create: {}", IOUtils.toString((InputStream) response.getEntity(), "UTF-8"));
        }
      } catch (IOException e) {
        logger.error("Failed to extract error message", e);
      }
      
      return null;
    }
    
    UUID locationId = getCreateResponseLocationId(response);
    if (locationId != null) {
      return locationId;
    }
    
    return getCreateResponseBodyId(response);
  }

  /**
   * Attempts to locate id from create location response
   * 
   * @param response response
   * @return id or null if not found
   */
  private static UUID getCreateResponseLocationId(javax.ws.rs.core.Response response) {
    String location = response.getHeaderString("location");
    if (StringUtils.isNotBlank(location)) {
      Pattern pattern = Pattern.compile(".*\\/(.*)$");
      Matcher matcher = pattern.matcher(location);
      
      if (matcher.find()) {
        return UUID.fromString(matcher.group(1));
      }
    }
    
    return null;
  }

  /**
   * Attempts to locate id from create response body
   * 
   * @param response response object
   * @return id or null if not found
   */
  private static UUID getCreateResponseBodyId(javax.ws.rs.core.Response response) {
    if (response.getEntity() instanceof InputStream) {
      try (InputStream inputStream = (InputStream) response.getEntity()) {
        Map<String, Object> result = readJsonMap(inputStream);
        if (result.get("_id") instanceof String) {
          return UUID.fromString((String) result.get("_id"));
        }
        
        if (result.get("id") instanceof String) {
          return UUID.fromString((String) result.get("id"));
        } 
      } catch (IOException e) {
        if (logger.isDebugEnabled()) {
          logger.debug("Failed to locate id from response", e);
        }
      }
    }
    
    return null;
  }

  /**
   * Returns impersonate server URL
   * 
   * @param settings settings
   * @return impersonate server URL
   */
  private static String getServerUrl(Map<String, String> settings) {
    return settings.get("oauth.keycloak.serverUrl");
  }

  /**
   * Returns impersonate realm
   * 
   * @param settings settings
   * @return impersonate realm
   */
  private static String getRealm(Map<String, String> settings) {
    return settings.get("oauth.keycloak.realm");
  }

  /**
   * Returns admin password
   * 
   * @param settings settings
   * @return admin password
   */
  private static String getAdminPassword(Map<String, String> settings) {
    return settings.get("oauth.keycloak.adminPassword");
  }

  /**
   * Returns admin user
   * 
   * @param settings settings
   * @return admin user
   */
  private static String getAdminUser(Map<String, String> settings) {
    return settings.get("oauth.keycloak.adminUser");
  }

  /**
   * Returns admin client id
   * 
   * @param settings settings
   * @return admin client id
   */
  private static String getAdminClientId(Map<String, String> settings) {
    return settings.get("oauth.keycloak.apiKey");
  }

  /**
   * Returns admin client secret
   * 
   * @param settings settings
   * @return admin client secret
   */
  private static String getAdminClientSecret(Map<String, String> settings) {
    return settings.get("oauth.keycloak.apiSecret");
  }

  /**
   * Returns impersonate client id
   * 
   * @param settings settings
   * @return impersonate client id
   */
  private static String getImpersonateClientId(Map<String, String> settings) {
    return settings.get("oauth.keycloak.impersonateClient");
  }

  /**
   * Returns impersonate client secret
   * 
   * @param settings settings
   * @return impersonate client secret
   */
  private static String getImpersonateClientSecret(Map<String, String> settings) {
    return settings.get("oauth.keycloak.impersonateClientSecret");
  }
  
  /**
   * Returns impersonate password
   * 
   * @param settings settings
   * @return impersonate password
   */
  private static String getImpersonatePassword(Map<String, String> settings) {
    return settings.get("oauth.keycloak.impersonatePass");
  }

  /**
   * Returns impersonate user
   * 
   * @param settings settings
   * @return impersonate user
   */
  private static String getImpersonateUser(Map<String, String> settings) {
    return settings.get("oauth.keycloak.impersonateUser");
  }
  
  /**
   * Returns impersonate redirect URL
   * 
   * @param settings settings
   * @return impersonate redirect URL
   */
  private static String getImpersonateRedirectUrl(Map<String, String> settings) {
    return "about:blank";
  }
  
}
