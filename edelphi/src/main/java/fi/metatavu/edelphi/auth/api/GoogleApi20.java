package fi.metatavu.edelphi.auth.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class GoogleApi20 extends DefaultApi20 {

	public static final String AUTHORIZATION_URL = "https://accounts.google.com/o/oauth2/auth?client_id=%s&response_type=code&redirect_uri=%s&scope=%s";
	public static final String TOKEN_URI = "https://accounts.google.com/o/oauth2/token";
  private static Logger logger = Logger.getLogger(GoogleApi20.class.getName());
      
  @Override
  public String getAccessTokenEndpoint() {
    return TOKEN_URI;
  }

  @Override
  public String getAuthorizationUrl(OAuthConfig config) {
    return String.format(AUTHORIZATION_URL, config.getApiKey(), encodeUrl(config.getCallback()), encodeUrl(config.getScope()));
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
    return new GoogleApi20ServiceImpl(this, config);
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
