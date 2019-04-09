package fi.metatavu.edelphi.auth;

import java.io.Serializable;
import java.util.Date;

public class OAuthAccessToken implements Serializable {
  
  private static final long serialVersionUID = -6305156458835722979L;
  
  private String externalId;
  private Date expires;
  private String token;
  private String refreshToken;
  private String[] scopes;
  
  public OAuthAccessToken(String externalId, String token, String refreshToken, Date expires, String[] scopes) {
	this.externalId = externalId;
    this.token = token;
    this.refreshToken = refreshToken;
    this.expires = expires;
    this.scopes = scopes;
  }
  
  public String getExternalId() {
	return externalId;
  }

  public Date getExpires() {
    return expires;
  }
  
  public String getToken() {
    return token;
  }
  
  public String getRefreshToken() {
    return refreshToken;
  }
  
  public String[] getScopes() {
    return scopes;
  }
}
