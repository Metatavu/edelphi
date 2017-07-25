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

	public static final String AUTHORIZATION_URL = "https://auth.metatavu.io/auth/realms/live-delphi/protocol/openid-connect/auth?client_id=%s&redirect_uri=%s&response_type=code";
	public static final String TOKEN_URI = "https://auth.metatavu.io/auth/realms/live-delphi/protocol/openid-connect/token";
  private static Logger logger = Logger.getLogger(KeycloakApi.class.getName());
  
  private String provider;
  
  public KeycloakApi(String provider) {
    this.provider = provider;
  }
      
  @Override
  public String getAccessTokenEndpoint() {
    return TOKEN_URI;
  }

  @Override
  public String getAuthorizationUrl(OAuthConfig config) {
    String url = String.format(AUTHORIZATION_URL, config.getApiKey(), encodeUrl(config.getCallback()));
    
    if (StringUtils.isNotBlank(provider)) {
      return String.format("%s&kc_idp_hint=%s", url, provider);  
    } else {
      return url;
    }
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
  
  private String encodeUrl(String url) {
    try {
      return URLEncoder.encode(url, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      logger.log(Level.SEVERE, String.format("Failed to url encode string %s", url), e);
    }
    
    return null;
  }
  
}
