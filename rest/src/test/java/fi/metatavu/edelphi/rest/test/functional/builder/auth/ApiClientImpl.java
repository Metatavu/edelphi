package fi.metatavu.edelphi.rest.test.functional.builder.auth;

import fi.metatavu.jaxrs.test.functional.builder.client.ApiClient;

public class ApiClientImpl implements ApiClient {
  
  private fi.metatavu.edelphi.rest.client.ApiClient apiClient;

  public ApiClientImpl(fi.metatavu.edelphi.rest.client.ApiClient apiClient) {
    this.apiClient = apiClient;
  }

}
