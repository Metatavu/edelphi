package fi.metatavu.edelphi.rest.test.functional.builder.impl;

import fi.metatavu.edelphi.rest.client.ApiClient;
import fi.metatavu.edelphi.rest.client.ApiClient.Api;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;

public abstract class ApiTestBuilderResource <T, A extends Api> extends fi.metatavu.jaxrs.test.functional.builder.AbstractApiTestBuilderResource <T, A, ApiClient> {

  private ApiClient apiClient;
  
  public ApiTestBuilderResource(AbstractTestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder);
    this.apiClient = apiClient;
  }

  @Override
  protected ApiClient getApiClient() {
    return apiClient;
  }
  
  @Override
  protected A getApi() {
    Class<A> apiClass = getApiClass();
    return getApiClient().buildClient(apiClass);
  }
  
}
