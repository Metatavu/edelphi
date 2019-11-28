package fi.metatavu.edelphi.rest.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.openapitools.client.api.QueryQuestionAnswersApi;
import org.openapitools.client.model.Panel;
import org.openapitools.client.model.QueryPageScale1d;
import org.openapitools.client.model.QueryQuestionAnswerScale1d;
import org.openapitools.client.model.QueryQuestionScale1dAnswerData;
import org.openapitools.client.model.QueryReply;

import feign.FeignException;
import fi.metatavu.edelphi.rest.client.ApiClient;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;

/**
 * Test builder resource for panels
 * 
 * @author Antti Leppä
 */
public class QueryScale1dAnswerTestBuilderResource extends ApiTestBuilderResource<QueryQuestionAnswerScale1d, QueryQuestionAnswersApi> {
  
  private Map<String, Long> panelIds = new HashMap<>();
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public QueryScale1dAnswerTestBuilderResource(AbstractTestBuilder<ApiClient> testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /**
   * Upserts reply into the server
   * 
   * @param panel panel
   * @param queryPage query panel
   * @param queryReply query reply
   * @param value value
   * @return updated answer
   */
  public QueryQuestionAnswerScale1d upsert(Panel panel, QueryPageScale1d queryPage, QueryReply queryReply, String value) {
    QueryQuestionScale1dAnswerData data = new QueryQuestionScale1dAnswerData();
    data.setValue(value);    
    QueryQuestionAnswerScale1d queryQuestionAnswerScale1d = new QueryQuestionAnswerScale1d();
    queryQuestionAnswerScale1d.setData(data);
    queryQuestionAnswerScale1d.setQueryPageId(queryPage.getId());
    queryQuestionAnswerScale1d.setQueryReplyId(queryReply.getId());
    QueryQuestionAnswerScale1d answer = getApi().upsertQueryQuestionAnswerScale1d(panel.getId(), getAnswerId(queryPage, queryReply), queryQuestionAnswerScale1d);
    
    panelIds.put(answer.getId(), panel.getId());
    
    return addClosable(answer);
  }
  
  /**
   * Finds an answer
   * 
   * @param panelId panel
   * @param queryPageId query page
   * @param queryReplyId query reply
   * @return answer
   */
  public QueryQuestionAnswerScale1d findAnswer(Panel panel, QueryPageScale1d queryPage, QueryReply queryReply) {
    return getApi().findQueryQuestionAnswerScale1d(panel.getId(), getAnswerId(queryPage.getId(), queryReply.getId()));
  }
  
  /**
   * Finds an answer
   * 
   * @param panelId panel id
   * @param queryPageId query page id
   * @param queryReplyId query reply id
   * @return answer
   */
  public QueryQuestionAnswerScale1d findAnswer(Long panelId, Long queryPageId, Long queryReplyId) {
    return getApi().findQueryQuestionAnswerScale1d(panelId, getAnswerId(queryPageId, queryReplyId));
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param panelId panel id
   * @param queryReplyId query reply id
   */
  public void assertFindFailStatus(int expectedStatus, Long panelId, Long queryPageId, Long queryReplyId) {
    try {
      findAnswer(panelId, queryPageId, queryReplyId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {  
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual panel equals expected panel when both are serialized into JSON
   * 
   * @param expected expected panel
   * @param actual actual panel
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertPanelsEqual(Panel expected, Panel actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }
  
  @Override
  public void clean(QueryQuestionAnswerScale1d answer) throws Exception {
    Long panelId = panelIds.get(answer.getId());
    getApi().deleteQueryQuestionAnswerScale1d(panelId, answer.getId());
  }

  /**
   * Returns answer id
   * 
   * @param queryPage page
   * @param queryReply reply
   * @return answer id
   */
  private String getAnswerId(QueryPageScale1d queryPage, QueryReply queryReply) {
    return getAnswerId(queryPage.getId(), queryReply.getId());
  }

  /**
   * Returns answer id
   * 
   * @param queryPageId page id
   * @param queryReplyId reply id
   * @return answer id
   */
  private String getAnswerId(Long queryPageId, Long queryReplyId) {
    return String.format("%d-%d", queryPageId, queryReplyId);
  }

}
