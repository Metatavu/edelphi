package fi.metatavu.edelphi.auth.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth20ServiceImpl;

import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import net.sf.json.JSONObject;

public class KeycloakServiceImpl extends OAuth20ServiceImpl {

  private static final Logger logger = Logger.getLogger(KeycloakServiceImpl.class.getName());
  private final OAuthConfig config;
  private final DefaultApi20 api;

  public KeycloakServiceImpl(DefaultApi20 api, OAuthConfig config) {
    super(api, config);

    this.api = api;
    this.config = config;
  }

  @Override
  public Token getAccessToken(Token requestToken, Verifier verifier) {
    OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
    request.addBodyParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
    request.addBodyParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
    request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
    request.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
    request.addBodyParameter("grant_type", "authorization_code");
    
    if (config.hasScope()) {
      request.addBodyParameter(OAuthConstants.SCOPE, config.getScope());
    }

    Response response = request.send();
    if (response.isSuccessful()) {
      JSONObject jsonObject = JSONObject.fromObject(response.getBody());
      return api.getAccessTokenExtractor().extract(jsonObject.toString(0));
    } else {
      logger.log(Level.SEVERE, () -> String.format("Failed to resolve access token [%d] %s", response.getCode(), response.getMessage()));
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_AUTHENTICATION_REQUEST, "Failed to resolve access token");
    }
  }
}
