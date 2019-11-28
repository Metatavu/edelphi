package fi.metatavu.edelphi.rest.test.functional.builder.auth;

import java.io.IOException;

import fi.metatavu.edelphi.rest.client.ApiClient;
import fi.metatavu.edelphi.rest.test.functional.builder.impl.PanelTestBuilderResource;
import fi.metatavu.edelphi.rest.test.functional.builder.impl.QueryReplyTestBuilderResource;
import fi.metatavu.edelphi.rest.test.functional.builder.impl.QueryScale1dAnswerTestBuilderResource;
import fi.metatavu.edelphi.rest.test.functional.builder.impl.QueryScale1dTestBuilderResource;
import fi.metatavu.edelphi.rest.test.functional.builder.impl.QuerySectionTestBuilderResource;
import fi.metatavu.edelphi.rest.test.functional.builder.impl.QueryTestBuilderResource;
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
  private QueryTestBuilderResource queries;
  private QuerySectionTestBuilderResource querySections;
  private QueryScale1dTestBuilderResource queryScale1d;
  private QueryReplyTestBuilderResource queryReplies;
  private QueryScale1dAnswerTestBuilderResource scale1dAnswers;
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder instance
   * @param accessTokenProvider access token provider
   */
  public TestBuilderAuthentication(AbstractTestBuilder<ApiClient> testBuilder, AccessTokenProvider accessTokenProvider) {
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
   * Returns test builder resource for query replies
   * 
   * @return test builder resource for query replies
   * @throws IOException thrown when authentication fails
   */
  public QueryReplyTestBuilderResource queryReplies() throws IOException {
    if (queryReplies != null) {
      return queryReplies;
    }
    
    return new QueryReplyTestBuilderResource(getTestBuilder(), createClient());
  }

  /**
   * Returns test builder resource for queries
   * 
   * @return test builder resource for queries
   * @throws IOException thrown when authentication fails
   */
  public QueryTestBuilderResource queries() throws IOException {
    if (queries != null) {
      return queries;
    }
    
    return new QueryTestBuilderResource(getTestBuilder(), createClient());
  }

  /**
   * Returns test builder resource for query sections
   * 
   * @return test builder resource for query sections
   * @throws IOException thrown when authentication fails
   */
  public QuerySectionTestBuilderResource querySections() throws IOException {
    if (querySections != null) {
      return querySections;
    }
    
    return new QuerySectionTestBuilderResource(getTestBuilder(), createClient());
  }
 
  /**
   * Returns test builder resource for query scale1d
   * 
   * @return test builder resource for query scale1d
   * @throws IOException thrown when authentication fails
   */
  public QueryScale1dTestBuilderResource queryScale1d() throws IOException {
    if (queryScale1d != null) {
      return queryScale1d;
    }
    
    return new QueryScale1dTestBuilderResource(getTestBuilder(), createClient());
  }
 
  /**
   * Returns test builder resource for scale1d answers
   * 
   * @return test builder resource for scale1d answers
   * @throws IOException thrown when authentication fails
   */
  public QueryScale1dAnswerTestBuilderResource scale1dAnswers() throws IOException {
    if (scale1dAnswers != null) {
      return scale1dAnswers;
    }
    
    return new QueryScale1dAnswerTestBuilderResource(getTestBuilder(), createClient());
  }
  
  /** 
   * Creates ApiClient authenticated by the given access token
   * 
   * @return ApiClient authenticated by the given access token
   * @throws IOException 
   */
  @Override
  protected ApiClient createClient(String accessToken) {
    String authorization = accessToken != null ? String.format("Bearer %s", accessToken) : null;
    ApiClient apiClient = accessToken != null ? new ApiClient("bearer", authorization) : new ApiClient();    
    String basePath = String.format("http://%s:%d/v1", TestSettings.getHost(), TestSettings.getPort());
    apiClient.setBasePath(basePath);
    return apiClient;
  }
  
}
