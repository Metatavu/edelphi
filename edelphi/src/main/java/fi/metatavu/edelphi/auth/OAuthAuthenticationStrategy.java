package fi.metatavu.edelphi.auth;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.oauth.OAuthService;

import fi.metatavu.edelphi.smvcj.controllers.RequestContext;

public abstract class OAuthAuthenticationStrategy extends AbstractAuthenticationStrategy {

  private static final String REQUESTED_SCOPES_ATTRIBUTE = ".requestedScopes";
  private String[] defaultScopes;

  public OAuthAuthenticationStrategy(String... defaultScopes) {
    this.defaultScopes = defaultScopes;
  }
  
  protected String[] getDefaultScopes() {
    return defaultScopes;
  }

  protected abstract Api getApi(Map<String, String> apiParams);

  protected abstract String getApiKey();

  protected abstract String getApiSecret();

  protected abstract AuthenticationResult processResponse(RequestContext requestContext, OAuthService service, String[] requestedScopes);
  
  @Override
  public boolean requiresCredentials() {
    return false;
  }
  
  @Override
  public void logout(RequestContext requestContext, String redirectUrl) {
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
      Map<String, String> apiParams = getResponseApiParams(requestContext);
      String[] requestedScopes = (String[]) session.getAttribute(String.format("%s%s", getName(), REQUESTED_SCOPES_ATTRIBUTE));
      session.removeAttribute(String.format("%s%s", getName(), REQUESTED_SCOPES_ATTRIBUTE));
      OAuthService service = getOAuthService(requestContext, apiParams, requestedScopes);
      return processResponse(requestContext, service, requestedScopes);
    }
  }

  protected abstract String getOAuthCallbackURL(RequestContext requestContext);
  
  protected OAuthService getOAuthService(RequestContext requestContext, Map<String, String> apiParams, String... scopes) {
    String apiKey = getApiKey();
    String apiSecret = getApiSecret();
    String callback = getOAuthCallbackURL(requestContext);
    Api api = getApi(apiParams);

    ServiceBuilder serviceBuilder = new ServiceBuilder()
      .provider(api)
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
  
  public void performDiscovery(RequestContext requestContext, String... scopes) {
    Map<String, String> apiParams = getDiscoveryApiParams(requestContext);
    OAuthService service = getOAuthService(requestContext, apiParams, scopes);
    String authUrl = service.getAuthorizationUrl(null);
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

  private Map<String, String> getResponseApiParams(RequestContext requestContext) {
    return null;
  }
  
  private Map<String, String> getDiscoveryApiParams(RequestContext requestContext) {
    Map<String, String> apiParams = new HashMap<>();
    String hint = requestContext.getString("hint");
    if (hint != null) {
      apiParams.put("hint", hint);
    }
    
    apiParams.put("locale", requestContext.getRequest().getLocale().getLanguage());
    
    return apiParams;
  }
}
