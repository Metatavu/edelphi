package fi.metatavu.edelphi.auth.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class KeycloakApi extends DefaultApi20 {
  
  private static Logger logger = Logger.getLogger(KeycloakApi.class.getName());
  
  private String serverUrl;
  private String realm;
  private String hint;
  private String locale;
  
  public KeycloakApi(String serverUrl, String realm, String hint, String locale) {
    this.serverUrl = serverUrl;
    this.realm = realm;
    this.hint = hint;
    this.locale = locale;
  }
      
  @Override
  public String getAccessTokenEndpoint() {
    return String.format("%s/realms/%s/protocol/openid-connect/token", getServerUrl(), getRealm());
  }

  @Override
  public String getAuthorizationUrl(OAuthConfig config) {
    if ("register".equals(hint)) {
      return getRegisterUrl(config);
    }
    
    String url = String.format("%s/realms/%s/protocol/openid-connect/auth?client_id=%s&redirect_uri=%s&response_type=code&ui_locales=%s", getServerUrl(), getRealm(), config.getApiKey(), encodeUrl(config.getCallback()), getLocale());
    
    if (StringUtils.isNotBlank(hint)) {
      return String.format("%s&kc_idp_hint=%s", url, hint);  
    } else {
      return url;
    }
  }
  
  private String getRegisterUrl(OAuthConfig config) {
    return String.format("%s/realms/%s/protocol/openid-connect/registrations?client_id=%s&redirect_uri=%s&response_type=code&scope=openid", getServerUrl(), getRealm(), config.getApiKey(), encodeUrl(config.getCallback()));
  }
  
  @Override
  public AccessTokenExtractor getAccessTokenExtractor() {
    return new JsonTokenExtractor();
  }

  @Override
  public Verb getAccessTokenVerb() {
    return Verb.POST;
  }

  @Override
  public OAuthService createService(OAuthConfig config) {
    return new KeycloakServiceImpl(this, config);
  }
  
  private String getServerUrl() {
    return serverUrl;
  }
  
  private String getRealm() {
    return realm;
  }
  
  private String getLocale() {
    return locale;
  }
  
  private String encodeUrl(String url) {
    try {
      return URLEncoder.encode(url, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      logger.log(Level.SEVERE, String.format("Failed to url encode string %s", url), e);
    }
    
    return null;
  }
  
}
