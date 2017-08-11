package fi.metatavu.edelphi.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.edelphi.auth.AuthenticationProviderFactory;
import fi.metatavu.edelphi.auth.KeycloakAuthenticationStrategy;
import fi.metatavu.edelphi.auth.OAuthAccessToken;
import fi.metatavu.edelphi.dao.base.DelfoiAuthDAO;
import fi.metatavu.edelphi.dao.base.DelfoiDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiAuth;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;

public class AuthUtils {
  
  private static final String PROVIDER_ACCESS_TOKENS = "%s.accessTokens";
  private static final String AUTHENTICATION_STRATEGIES = "authenticationStrategies";
  private static final String LOGIN_CONTEXT_ID = "loginContextId";
  private static final String LOGIN_CONTEXT_TYPE = "loginContextType";
  private static final String AUTH_SOURCE_ID = "authSourceId";
  private static final String LOGIN_REDIRECT_URL = "loginRedirectUrl";
  private static final String INTERNAL_AUTHORIZATION_HEADER = "InternalAuthorization ";
  private static final Logger logger = Logger.getLogger(AuthUtils.class.getName());

  private AuthUtils() {
  }
  
  public static AuthSource getAuthSource(String strategy) {
    DelfoiDAO delfoiDAO = new DelfoiDAO();
    DelfoiAuthDAO delfoiAuthDAO = new DelfoiAuthDAO();
    
    Delfoi delfoi = delfoiDAO.findById(1l);
    List<DelfoiAuth> delfoiAuths = delfoiAuthDAO.listByDelfoi(delfoi);
    for (DelfoiAuth delfoiAuth : delfoiAuths) {
      if (strategy.equals(delfoiAuth.getAuthSource().getStrategy())) {
        return delfoiAuth.getAuthSource();
      }
    }
    
    return null;
  }
  
  public static KeycloakAuthenticationStrategy getKeycloakStrategy() {
    AuthSource keycloakAuthSource = getAuthSource("Keycloak");
    if (keycloakAuthSource == null) {
      logger.log(Level.SEVERE, "Could not create Keycloak strategy because auth source is not configured");
      return null;
    }
    
    return (KeycloakAuthenticationStrategy) AuthenticationProviderFactory.getInstance().createAuthenticationProvider(keycloakAuthSource);    
  }
  
  public static void storeRedirectUrl(RequestContext requestContext, String redirectUrl) {
    HttpSession session = requestContext.getRequest().getSession();
    session.setAttribute(LOGIN_REDIRECT_URL, redirectUrl);
  }

  public static String retrieveRedirectUrl(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    String redirectUrl = (String) session.getAttribute(LOGIN_REDIRECT_URL);
    session.removeAttribute(LOGIN_REDIRECT_URL);
    return redirectUrl;
  }
  
  public static void storeAuthSourceId(RequestContext requestContext, Long authSourceId) {
    HttpSession session = requestContext.getRequest().getSession();
    session.setAttribute(AUTH_SOURCE_ID, authSourceId);
  }

  public static Long retrieveAuthSourceId(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    Long authSourceId = (Long) session.getAttribute(AUTH_SOURCE_ID);
    session.removeAttribute(AUTH_SOURCE_ID);
    return authSourceId;
  }

  public static String getLoginContextType(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    String contextType = (String) session.getAttribute(LOGIN_CONTEXT_TYPE);
    contextType = contextType == null ? "DELFOI" : contextType;
    return contextType;
  }

  public static Long getLoginContextId(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    String loginContextId = (String) session.getAttribute(LOGIN_CONTEXT_ID);
    return NumberUtils.isNumber(loginContextId) ? new Long(loginContextId) : RequestUtils.getDelfoi(requestContext).getId();
  }
  
  @SuppressWarnings("unchecked")
  public static void addAuthenticationStrategy(RequestContext requestContext, String strategy) {
    HttpSession session = requestContext.getRequest().getSession();
    Set<String> authenticationStrategies = (Set<String>) session.getAttribute(AUTHENTICATION_STRATEGIES);
    if (authenticationStrategies == null) {
      authenticationStrategies = new HashSet<>();
    }
    if (!authenticationStrategies.contains(strategy)) {
      authenticationStrategies.add(strategy);
    }
    session.setAttribute(AUTHENTICATION_STRATEGIES, authenticationStrategies); 
  }
  
  @SuppressWarnings("unchecked")
  public static boolean isAuthenticatedBy(RequestContext requestContext, String strategy) {
    HttpSession session = requestContext.getRequest().getSession();
    Set<String> authenticationStrategies = (Set<String>) session.getAttribute(AUTHENTICATION_STRATEGIES);
    return authenticationStrategies != null && authenticationStrategies.contains(strategy);
  }
  
  public static void storeOAuthAccessToken(RequestContext requestContext, String provider, OAuthAccessToken token) {
    HttpSession session = requestContext.getRequest().getSession();
    OAuthAccessToken[] accessTokens = (OAuthAccessToken[]) session.getAttribute(String.format(PROVIDER_ACCESS_TOKENS, provider));
    if (accessTokens == null) {
      session.setAttribute(String.format(PROVIDER_ACCESS_TOKENS, provider), new OAuthAccessToken[]{token});
    } else {
      OAuthAccessToken[] newTokens = new OAuthAccessToken[accessTokens.length + 1];
      for (int i = 0, l = accessTokens.length; i < l; i++) {
        newTokens[i] = accessTokens[i];
      }
      newTokens[newTokens.length - 1] = token;
      session.setAttribute(String.format(PROVIDER_ACCESS_TOKENS, provider), newTokens);
    }
  }

  public static OAuthAccessToken getOAuthAccessToken(RequestContext requestContext, String provider, String... scopes) {
    HttpSession session = requestContext.getRequest().getSession();
    
    OAuthAccessToken[] accessTokens = (OAuthAccessToken[]) session.getAttribute(String.format(PROVIDER_ACCESS_TOKENS, provider));
    if (accessTokens != null) {
      for (OAuthAccessToken accessToken : accessTokens) {
        List<String> accessTokenScopes = accessToken.getScopes() != null ? Arrays.asList(accessToken.getScopes()) : Collections.emptyList();
        if (scopes == null || accessTokenScopes.containsAll(Arrays.asList(scopes))) {
      		return accessToken;
      	}
      }
    }
    
    return null;
  }
  
  public static boolean isGrantedOAuthScope(RequestContext requestContext, String provider, String scope) {
    return getOAuthAccessToken(requestContext, provider, scope) != null;
  }
  
  public static boolean isOAuthTokenExpired(OAuthAccessToken accessToken) {
    return (accessToken.getExpires() != null) && (accessToken.getExpires().getTime() < System.currentTimeMillis());
  }
  
  public static String getInternalAuthorization(HttpServletRequest request) {
    String authorizationHeader = request.getHeader("Authorization");
    if (!StringUtils.startsWith(authorizationHeader, INTERNAL_AUTHORIZATION_HEADER)) {
      return null;
    }
    
    return authorizationHeader.substring(INTERNAL_AUTHORIZATION_HEADER.length());
  }

}
