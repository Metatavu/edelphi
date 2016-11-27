package fi.metatavu.edelphi.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import fi.metatavu.edelphi.smvcj.controllers.RequestContext;


public class FacebookAuthenticationStrategy extends OAuthAuthenticationStrategy {

  public FacebookAuthenticationStrategy() {
    super("email");
  }
  
  @Override
  public String getName() {
    return "Facebook";
  }

  @Override
  protected String getApiKey() {
    return settings.get("oauth.facebook.apiKey");
  }

  @Override
  protected String getApiSecret() {
    return settings.get("oauth.facebook.apiSecret");
  }

  @Override
  protected String getOAuthCallbackURL(RequestContext requestContext) {
    return settings.get("oauth.facebook.callbackUrl");
  }
  
  @Override
  protected Class<? extends Api> getApiClass() {
    return FacebookApi.class;
  }

  @Override
  protected AuthenticationResult processResponse(RequestContext requestContext, OAuthService service, String[] requestedScopes) {
    String verifier = requestContext.getString("code");
    
    Verifier v = new Verifier(verifier);
    Token accessToken = service.getAccessToken(null, v);

    OAuthRequest request = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
    service.signRequest(accessToken, request); // the access token from step 4
    Response response = request.send();
    
    JSONObject o = JSONObject.fromObject(response.getBody());
    
    String externalId = o.getString("id");
    String emailAddr = o.optString("email");
    String firstName = o.optString("first_name");
    String lastName = o.optString("last_name");
    String fullName = o.optString("name");
    
    if (StringUtils.isBlank(firstName) && StringUtils.isBlank(lastName) && StringUtils.isNotBlank(fullName)) {
      firstName = extractFirstName(fullName);
      lastName = extractLastName(fullName);
    }

    List<String> emails = new ArrayList<>();
    if (StringUtils.isNotBlank(emailAddr)) {
      emails.add(emailAddr);
    }
    
    return processExternalLogin(requestContext, externalId, emails, firstName, lastName);
  }

  @Override
  public String[] getKeys() {
    return new String[] {"oauth.facebook.apiKey", "oauth.facebook.apiSecret", "oauth.facebook.callbackUrl"};
  }

  @Override
  public String localizeKey(Locale locale, String key) {
    return key;
  }
}
