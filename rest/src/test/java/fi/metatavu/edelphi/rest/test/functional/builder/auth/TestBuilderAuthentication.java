package fi.metatavu.edelphi.rest.test.functional.builder.auth;

import java.io.IOException;

import fi.metatavu.edelphi.rest.client.ApiClient;
import fi.metatavu.edelphi.rest.test.functional.builder.impl.PanelTestBuilderResource;
import fi.metatavu.edelphi.rest.test.functional.settings.TestSettings;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication;

/**
 * Test builder authentication
 * 
 * @author Antti Leppä
 */
public class TestBuilderAuthentication extends AuthorizedTestBuilderAuthentication<ApiClient> {

  private PanelTestBuilderResource panels;
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder instance
   * @param accessTokenProvider access token provider
   */
  public TestBuilderAuthentication(AbstractTestBuilder testBuilder, AccessTokenProvider accessTokenProvider) {
    super(testBuilder, accessTokenProvider);
  }
 
  /**
   * Returns test builder resource for panels
   * 
   * @return test builder resource for panels
   * @throws IOException thrown when authentication fails
   */
  public PanelTestBuilderResource panels() throws IOException {
    if (panels != null) {
      return panels;
    }
    
    return new PanelTestBuilderResource(getTestBuilder(), createClient());
  }
  
  /** 
   * Creates ApiClient authenticated by the given access token
   * 
   * @return ApiClient authenticated by the given access token
   * @throws IOException 
   */
  @Override
  protected ApiClient createClient(String accessToken) throws IOException {
    String authorization = accessToken != null ? String.format("Bearer %s", accessToken) : null;
    ApiClient apiClient = accessToken != null ? new ApiClient("bearer", authorization) : new ApiClient();    
    String basePath = String.format("http://%s:%d/v1", TestSettings.getHost(), TestSettings.getPort());
    apiClient.setBasePath(basePath);
    return apiClient;
  }
  
}
