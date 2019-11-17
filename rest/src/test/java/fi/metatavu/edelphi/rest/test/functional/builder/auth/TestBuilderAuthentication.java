package fi.metatavu.edelphi.rest.test.functional.builder.auth;

import java.io.IOException;

import fi.metatavu.edelphi.rest.test.functional.settings.TestSettings;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication;
import fi.metatavu.jaxrs.test.functional.builder.client.ApiClient;

public class TestBuilderAuthentication extends AuthorizedTestBuilderAuthentication {

  public TestBuilderAuthentication(AbstractTestBuilder testBuilder, AccessTokenProvider accessTokenProvider) {
    super(testBuilder, accessTokenProvider);
  }

  /** 
   * Creates ApiClient authenticated by the given access token
   * 
   * @return ApiClient authenticated by the given access token
   * @throws IOException 
   */
  @Override
  protected ApiClient createClient(String accessToken) throws IOException {
    fi.metatavu.edelphi.rest.client.ApiClient apiClient = new fi.metatavu.edelphi.rest.client.ApiClient();
    
    apiClient.setBasePath("/v1");
    apiClient.setHost(TestSettings.getHost());
    apiClient.setPort(TestSettings.getPort());
    
    if (accessToken != null) {
      apiClient.setRequestInterceptor(builder -> {
        builder.header("Authorization", String.format("Bearer %s", accessToken));
      });
    }
    
    return new ApiClientImpl(apiClient);
  }
  
}
