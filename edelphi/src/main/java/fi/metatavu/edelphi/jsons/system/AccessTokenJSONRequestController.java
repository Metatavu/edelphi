package fi.metatavu.edelphi.jsons.system;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import fi.metatavu.edelphi.auth.OAuthAccessToken;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.utils.AuthUtils;

/**
 * Returns logged user's access token
 * 
 * @author Antti Leppä
 */
public class AccessTokenJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext requestContext) {
    OAuthAccessToken keycloakToken = AuthUtils.getOAuthAccessToken(requestContext, "Keycloak");
    if (keycloakToken != null) {
      String expires = OffsetDateTime.ofInstant(keycloakToken.getExpires().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME);
      requestContext.addResponseParameter("expires", expires);
      requestContext.addResponseParameter("token", keycloakToken.getToken());
    }    
  }
}