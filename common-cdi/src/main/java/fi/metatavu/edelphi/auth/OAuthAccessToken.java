package fi.metatavu.edelphi.auth;

import java.io.Serializable;
import java.util.Date;

/**
 * OAuth access token
 */
public class OAuthAccessToken implements Serializable {

    private static final long serialVersionUID = -6305156458835722979L;

    private final String externalId;
    private final Date expires;
    private final String accessToken;
    private final String refreshToken;

    private final String idToken;
    private final String[] scopes;

    /**
     * Constructor
     *
     * @param externalId external id
     * @param accessToken access token
     * @param refreshToken refresh token (optional)
     * @param idToken id token (optional)
     * @param expires expiration date
     * @param scopes scopes
     */
    public OAuthAccessToken(String externalId, String accessToken, String refreshToken, String idToken, Date expires, String[] scopes) {
        this.externalId = externalId;
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.expires = expires;
        this.scopes = scopes;
    }

    /**
     * Returns external id
     *
     * @return external id
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Returns expiration date
     *
     * @return expiration date
     */
    public Date getExpires() {
        return expires;
    }

    /**
     * Returns access token
     *
     * @return access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Returns id token
     *
     * @return id token
     */
    public String getIdToken() {
        return idToken;
    }

    /**
     * Returns refresh token
     *
     * @return refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Returns scopes
     *
     * @return scopes
     */
    public String[] getScopes() {
        return scopes;
    }
}
