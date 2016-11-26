package fi.metatavu.edelphi.auth;

import java.io.Serializable;
import java.util.Date;

public class OAuthAccessToken implements Serializable {
  
  private static final long serialVersionUID = -6305156458835722979L;
  
  private Date expires;
  private String token;
  private String[] scopes;
  
  public OAuthAccessToken(String token, Date expires, String[] scopes) {
    this.token = token;
    this.expires = expires;
    this.scopes = scopes;
  }

  public Date getExpires() {
    return expires;
  }
  
  public String getToken() {
    return token;
  }
  
  public String[] getScopes() {
    return scopes;
  }
}
