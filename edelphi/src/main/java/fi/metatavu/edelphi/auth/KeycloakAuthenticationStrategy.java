package fi.metatavu.edelphi.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scribe.builder.api.Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.edelphi.auth.api.KeycloakApi;
import fi.metatavu.edelphi.auth.api.KeycloakBrokerToken;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.AuthUtils;
import net.sf.json.JSONObject;

public class KeycloakAuthenticationStrategy extends OAuthAuthenticationStrategy {
  
  private static final Logger logger = Logger.getLogger(KeycloakAuthenticationStrategy.class.getName());

  @Override
  public String getName() {
    return "Keycloak";
  }

  @Override
  protected String getApiKey() {
    return settings.get("oauth.keycloak.apiKey");
  }

  @Override
  protected String getApiSecret() {
    return settings.get("oauth.keycloak.apiSecret");
  }

  @Override
  protected String getOAuthCallbackURL(RequestContext requestContext) {
    return settings.get("oauth.keycloak.callbackUrl");
  }
  
  private String getUserinfoUrl() {
    return settings.get("oauth.keycloak.userinfoUrl");
  }
  
  private String getLogoutUrl(String redirectUrl) {
    try {
      return String.format(settings.get("oauth.keycloak.logoutTemplate"), URLEncoder.encode(redirectUrl, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      logger.log(Level.SEVERE, "Failed to encode Keycloak logout URL", e);
      return null;
    }
  }

  private String getTokenUrl(String broker) {
    return String.format(settings.get("oauth.keycloak.tokenTemplate"), broker);
  }
  
  @Override
  public void logout(RequestContext requestContext, String redirectUrl) {
    requestContext.setRedirectURL(getLogoutUrl(redirectUrl));
  }
  
  @Override
  protected Api getApi(Map<String, String> apiParams) {
    String provider = apiParams != null ? apiParams.get("provider") : null;
    return new KeycloakApi(provider);
  }

  @Override
  protected AuthenticationResult processResponse(RequestContext requestContext, OAuthService service, String[] requestedScopes) {
    String code = requestContext.getString("code");

    Verifier verifier = new Verifier(code);
    Token accessToken = service.getAccessToken(null, verifier);
    Date expiresAt = getExpiresAt(accessToken);

    Response response = doSignedGet(accessToken, getUserinfoUrl());
    
    AuthUtils.storeOAuthAccessToken(requestContext, getName(), new OAuthAccessToken(accessToken.getToken(), expiresAt, requestedScopes));
    
    ObjectMapper objectMapper = new ObjectMapper();
    try (InputStream stream = response.getStream()) {
      UserInfo userInfo = objectMapper.readValue(stream, UserInfo.class);
      String externalId = userInfo.getSub();
      List<String> emails = Arrays.asList(userInfo.getEmail());
      String firstName = userInfo.getGivenName();
      String lastName = userInfo.getFamilyName();
      
      return processExternalLogin(requestContext, externalId, emails, firstName, lastName);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to read user info", e);
      return null;
    }
  }
  
  public OAuthAccessToken getBrokerToken(RequestContext requestContext, String broker) {
    String url = getTokenUrl(broker);
    
    OAuthAccessToken storedBrokerToken = AuthUtils.getOAuthAccessToken(requestContext, broker);
    if (storedBrokerToken != null) {
      return storedBrokerToken;
    }
    
    OAuthAccessToken keycloakToken = AuthUtils.getOAuthAccessToken(requestContext, getName());
    Response response = doSignedGet(keycloakToken, url);
    ObjectMapper objectMapper = new ObjectMapper();
    OAuthAccessToken resolvedBrokerToken = null;
    
    if (!response.isSuccessful()) {
      return null;
    }
    
    try (InputStream stream = response.getStream()) {
      KeycloakBrokerToken brokerToken = objectMapper.readValue(stream, KeycloakBrokerToken.class);
      Date expiresAt = getExpiresAt(brokerToken.getExpiresIn());
      resolvedBrokerToken = new OAuthAccessToken(brokerToken.getAccessToken(), expiresAt, null);
      AuthUtils.storeOAuthAccessToken(requestContext, getName(), resolvedBrokerToken);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to process broker token response", e);
    }

    return resolvedBrokerToken;
  }
  
  @Override
  public String[] getKeys() {
    return new String[] { "oauth.keycloak.apiKey", "oauth.keycloak.apiSecret", "oauth.keycloak.callbackUrl" };
  }

  @Override
  public String localizeKey(Locale locale, String key) {
    return key;
  }

  private Response doSignedGet(OAuthAccessToken accessToken, String url) {
    return doSignedGet(accessToken.getToken(), url);
  }
  
  private Response doSignedGet(Token accessToken, String url) {
    return doSignedGet(accessToken.getToken(), url);
  }

  private Response doSignedGet(String accessToken, String url) {
    OAuthRequest request = new OAuthRequest(Verb.GET, url);
    request.addHeader("Authorization", String.format("Bearer %s", accessToken));
    return request.send();
  }
  
  private Date getExpiresAt(Token accessToken) {
    JSONObject rawJson = JSONObject.fromObject(accessToken.getRawResponse());
    int expiresIn = rawJson.getInt("expires_in");
    return getExpiresAt(expiresIn);
  }
  
  private Date getExpiresAt(int expiresIn) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(new Date());
    calendar.add(Calendar.SECOND, expiresIn);
    return calendar.getTime();
  }
  
  public static class UserInfo {
    private String sub;

    private String name;

    @JsonProperty("preferred_username")
    private String preferredUsername;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    private String email;

    public String getSub() {
      return sub;
    }

    public void setSub(String sub) {
      this.sub = sub;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPreferredUsername() {
      return preferredUsername;
    }

    public void setPreferredUsername(String preferredUsername) {
      this.preferredUsername = preferredUsername;
    }

    public String getGivenName() {
      return givenName;
    }

    public void setGivenName(String givenName) {
      this.givenName = givenName;
    }

    public String getFamilyName() {
      return familyName;
    }

    public void setFamilyName(String familyName) {
      this.familyName = familyName;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

  }
}
