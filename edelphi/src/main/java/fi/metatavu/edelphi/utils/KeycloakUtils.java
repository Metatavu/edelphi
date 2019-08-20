package fi.metatavu.edelphi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    
    return new UUID(0L, 0L);
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
