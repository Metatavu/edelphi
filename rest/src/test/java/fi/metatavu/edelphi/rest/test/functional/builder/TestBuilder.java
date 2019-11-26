package fi.metatavu.edelphi.rest.test.functional.builder;

import java.io.IOException;

import fi.metatavu.edelphi.rest.test.functional.builder.auth.TestBuilderAuthentication;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.jaxrs.test.functional.builder.auth.KeycloakAccessTokenProvider;

/**
 * TestBuilder implementation
 * 
 * @author Antti Leppä
 */
public class TestBuilder extends AbstractTestBuilder {

  private static final String AUTH_SERVER_HOST = "test-edelphi-keycloak";
  private static final String AUTH_SERVER_URL = String.format("http://%s:8080/auth", AUTH_SERVER_HOST);
  private static final String REALM = "edelphi";
  private static final String CLIENT_ID = "ui";
  private static final String ADMIN_USER = "admin";
  private static final String ADMIN_PASSWORD = "admin";
  private static final String CLIENT_SECRET = null;
  
  private TestBuilderAuthentication admin;
  
  @Override
  public TestBuilderAuthentication createTestBuilderAuthentication(AbstractTestBuilder testBuilder, AccessTokenProvider accessTokenProvider) {
    return new TestBuilderAuthentication(testBuilder, accessTokenProvider);
  }

  /**
   * Returns admin authenticated authentication resource
   * 
   * @return admin authenticated authentication resource
   * @throws IOException 
   */
  public TestBuilderAuthentication admin() throws IOException {
    if (admin != null) {
      return admin;
    }
    
    admin = new TestBuilderAuthentication(this, new KeycloakAccessTokenProvider(AUTH_SERVER_URL, REALM, CLIENT_ID, ADMIN_USER, ADMIN_PASSWORD, CLIENT_SECRET));
    
    return admin;
  }
  
}
