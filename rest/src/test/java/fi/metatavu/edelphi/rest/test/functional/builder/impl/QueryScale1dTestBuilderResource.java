package fi.metatavu.edelphi.rest.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.openapitools.client.api.QueryPagesApi;
import org.openapitools.client.model.Panel;
import org.openapitools.client.model.Query;
import org.openapitools.client.model.QueryPageCommentOptions;
import org.openapitools.client.model.QueryPageLive2d;
import org.openapitools.client.model.QueryPageScale1d;
import org.openapitools.client.model.QueryPageScale1dAnswerType;
import org.openapitools.client.model.QueryPageType;
import org.openapitools.client.model.QuerySection;

import feign.FeignException;
import fi.metatavu.edelphi.rest.client.ApiClient;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;

/**
 * Test builder resource for panels
 * 
 * @author Antti Leppä
 */
public class QueryScale1dTestBuilderResource extends ApiTestBuilderResource<QueryPageScale1d, QueryPagesApi> {
  
  private Map<Long, Long> panelIds = new HashMap<>();
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public QueryScale1dTestBuilderResource(AbstractTestBuilder<ApiClient> testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /**
   * Creates new query page with default values
   * 
   * @param panel panel
   * @param query query
   * @param querySection query section
   * @return created query page
   * @throws ApiException 
   */
  public QueryPageScale1d create(Panel panel, Query query, QuerySection querySection) {
    QueryPageCommentOptions commentOptions = new QueryPageCommentOptions();
    commentOptions.setCommentable(true);
    commentOptions.setDiscussionVisible(true);
    List<String> options = Arrays.asList("---", "--", "-", "-/+", "+", "++", "+++");
    String label = "Label";
    String title = "Title";
    return create(panel, query, querySection, QueryPageScale1dAnswerType.SLIDER, commentOptions, label, options, 1, title);
  }
  
  /**
   * Creates new query page with default values
   * 
   * @param panel panel
   * @param query query
   * @param querySection query section
   * @param answerType  answer type
   * @param commentOptions comment options
   * @param label label
   * @param options options
   * @param pageNumber page number
   * @param title title
   * @return created panel
   * @throws ApiException 
   */
  public QueryPageScale1d create(Panel panel, Query query, QuerySection querySection, QueryPageScale1dAnswerType answerType, QueryPageCommentOptions commentOptions, String label, List<String> options, Integer pageNumber, String title) {
    QueryPageScale1d queryPage = new QueryPageScale1d();
    queryPage.setAnswerType(answerType);
    queryPage.setCommentOptions(commentOptions);
    queryPage.setLabel(label);
    queryPage.setOptions(options);
    queryPage.setPageNumber(pageNumber);
    queryPage.setTitle(title);
    queryPage.setQuerySectionId(querySection.getId());
    queryPage.setQueryId(query.getId());
    queryPage.setType(QueryPageType.THESIS_SCALE_1D);
    QueryPageScale1d result = getApi().createQueryPageScale1d(panel.getId(), queryPage);
    panelIds.put(result.getId(), panel.getId());
    return addClosable(result);
  }
  
  /**
   * Finds a query page
   * 
   * @param panelId panel id
   * @return found query page
   * @throws ApiException 
   */
  public QueryPageScale1d findPanel(Long panelId, Long queryPageId) {
    return getApi().findQueryPageScale1d(panelId, queryPageId);
  }
  
  /**
   * Updates an query page into the API
   * 
   * @param body body payload
   * @throws ApiException 
   */
  public QueryPageLive2d updateQueryPage(Long panelId, QueryPageScale1d body) {
    return getApi().updateQueryPageScale1d(panelId, body.getId(), body);
  }
  
  /**
   * Deletes a panel from the API
   * 
   * @param panel panel to be deleted
   * @param queryPageId query page id
   * @throws ApiException 
   */
  public void delete(Panel panel, Long queryPageId) {
    getApi().deleteQueryPage(panel.getId(), queryPageId);
    
    removeCloseable(closable -> {
      if (!(closable instanceof Panel)) {
        return false;
      }

      Panel closeablePanel = (Panel) closable;
      return closeablePanel.getId().equals(panel.getId());
    });
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param panelId panel id
   * @param queryPageId query page id
   */
  public void assertFindFailStatus(int expectedStatus, Long panelId, Long queryPageId) {
    try {
      getApi().findQueryPageScale1d(panelId, queryPageId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {  
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param panel panel
   * @param answerType answer type
   * @param commentOptions comment options
   * @param label label
   * @param options options
   * @param pageNumber pageNumber
   * @param title title
   */
  public void assertCreateFailStatus(int expectedStatus, Panel panel, Query query, QuerySection querySection, QueryPageScale1dAnswerType answerType, QueryPageCommentOptions commentOptions, String label, List<String> options, Integer pageNumber, String title) {
    try {
      create(panel, query, querySection, answerType, commentOptions, label, options, pageNumber, title);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param queryPageScale1d scale
   */
  public void assertUpdateFailStatus(int expectedStatus, QueryPageScale1d body) {
    try {
      Long panelId = panelIds.get(body.getId());
      updateQueryPage(panelId, body);
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param body scale
   */
  public void assertDeleteFailStatus(int expectedStatus, QueryPageScale1d body) {
    try {
      Long panelId = panelIds.get(body.getId());
      getApi().deleteQueryPage(panelId, body.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
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
  public void clean(QueryPageScale1d queryPageScale1d) {
    Long panelId = panelIds.get(queryPageScale1d.getId());
    getApi().deleteQueryPage(panelId, queryPageScale1d.getId());
  }

}
