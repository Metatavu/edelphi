package fi.metatavu.edelphi.auth;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import fi.metatavu.edelphi.smvcj.controllers.RequestContext;

public abstract class OAuthAuthenticationStrategy extends AbstractAuthenticationStrategy {

  private static final String OAUTH_REQUEST_TOKEN_ATTRIBUTE = "OAuthRequestToken";
  private static final String REQUESTED_SCOPES_ATTRIBUTE = ".requestedScopes";
  private String[] defaultScopes;

  public OAuthAuthenticationStrategy(String... defaultScopes) {
    this.defaultScopes = defaultScopes;
  }
  
  protected String[] getDefaultScopes() {
    return defaultScopes;
  }

  protected abstract Class<? extends Api> getApiClass();

  protected abstract String getApiKey();

  protected abstract String getApiSecret();

  protected abstract AuthenticationResult processResponse(RequestContext requestContext, OAuthService service, String[] requestedScopes);
  
  @Override
  public boolean requiresCredentials() {
    return false;
  }

  @Override
  public AuthenticationResult processLogin(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    if (!"rsp".equals(requestContext.getString("_stg"))) {
      String[] scopes;
      
      String[] extraScopes = requestContext.getStrings("extraScope");
      if ((extraScopes != null) && (extraScopes.length > 0)) {
        int defaultScopesLength = getDefaultScopes() != null ? getDefaultScopes().length : 0;
        int extraScopesLength = extraScopes.length;
        scopes = new String[defaultScopesLength + extraScopesLength];
        for (int i = 0; i < defaultScopesLength; i++) {
          scopes[i] = getDefaultScopes()[i];
        }

        for (int i = 0; i < extraScopesLength; i++) {
          scopes[i + defaultScopesLength] = extraScopes[i];
        }
      } else {
        scopes = requestContext.getStrings("scope");
      }
      
      if (scopes == null) {
        scopes = defaultScopes;
      }
      
      session.setAttribute(String.format("%s%s", getName(), REQUESTED_SCOPES_ATTRIBUTE), scopes);
      performDiscovery(requestContext, scopes);
      
      return AuthenticationResult.PROCESSING;
    } else {
      String[] requestedScopes = (String[]) session.getAttribute(String.format("%s%s", getName(), REQUESTED_SCOPES_ATTRIBUTE));
      session.removeAttribute(String.format("%s%s", getName(), REQUESTED_SCOPES_ATTRIBUTE));
      OAuthService service = getOAuthService(requestContext, requestedScopes);
      return processResponse(requestContext, service, requestedScopes);
    }
  }

  protected abstract String getOAuthCallbackURL(RequestContext requestContext);
  
  protected OAuthService getOAuthService(RequestContext requestContext, String... scopes) {
    String apiKey = getApiKey();
    String apiSecret = getApiSecret();
    String callback = getOAuthCallbackURL(requestContext);
    Class<? extends Api> apiClass = getApiClass();

    ServiceBuilder serviceBuilder = new ServiceBuilder()
      .provider(apiClass)
      .apiKey(apiKey)
      .apiSecret(apiSecret)
      .callback(callback);
    
    if (scopes != null && scopes.length > 0) {
      StringBuilder scopeBuilder = new StringBuilder();
      for (int i = 0, l = scopes.length; i < l;i++) {
        scopeBuilder.append(scopes[i]);
        if (i < (l - 1))
          scopeBuilder.append(' ');
      }
      serviceBuilder = serviceBuilder.scope(scopeBuilder.toString());
    }
    
    return serviceBuilder.build();
  }
  
  protected void setRequestToken(RequestContext requestContext, Token requestToken) {
    HttpSession session = requestContext.getRequest().getSession();

    if (requestToken != null)
      session.setAttribute(OAUTH_REQUEST_TOKEN_ATTRIBUTE, requestToken);
    else
      session.removeAttribute(OAUTH_REQUEST_TOKEN_ATTRIBUTE);
  }

  protected Token getRequestToken(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    
    return (Token) session.getAttribute(OAUTH_REQUEST_TOKEN_ATTRIBUTE);
  }
  
  public void performDiscovery(RequestContext requestContext, String... scopes) {
    OAuthService service = getOAuthService(requestContext, scopes);
    
    Token requestToken = null;
    boolean isV1 = DefaultApi10a.class.isAssignableFrom(getApiClass());

    // For OAuth version 1 the request token is fetched, for v2 it's not  
    if (isV1)
      requestToken = service.getRequestToken();

    String authUrl = service.getAuthorizationUrl(requestToken);

    setRequestToken(requestContext, requestToken);

    requestContext.setRedirectURL(authUrl);
  }

  /**
   * Extracts a last name of full name string
   * 
   * @param name full name
   * @return last name if it could be extracted
   */
  protected String extractLastName(String name) {
    if (StringUtils.isBlank(name)) {
      return null;
    }
   
    int lastIndexOf = name.lastIndexOf(' ');
    
    if (lastIndexOf == -1)
      return null;
    else
      return name.substring(lastIndexOf + 1);
  }
  
  /**
   * Extracts a first name of full name string
   * 
   * @param name full name
   * @return first name if it could be extracted
   */
  protected String extractFirstName(String name) {
    if (StringUtils.isBlank(name)) {
      return null;
    }
    
    int lastIndexOf = name.lastIndexOf(' ');
    
    if (lastIndexOf == -1)
      return null;
    else
      return name.substring(0, lastIndexOf);
  }
}
