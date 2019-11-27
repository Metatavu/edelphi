package fi.metatavu.edelphi.rest.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.openapitools.client.api.QueriesApi;
import org.openapitools.client.model.Panel;
import org.openapitools.client.model.Query;
import org.openapitools.client.model.QueryState;

import feign.FeignException;
import fi.metatavu.edelphi.rest.client.ApiClient;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;

/**
 * Test builder resource for panels
 * 
 * @author Antti Leppä
 */
public class QueryTestBuilderResource extends ApiTestBuilderResource<Query, QueriesApi> {
  
  private Map<Long, Long> panelIds = new HashMap<>();
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public QueryTestBuilderResource(AbstractTestBuilder<ApiClient> testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /**
   * Creates new query with default values
   *  
   * @param panel panel
   * @return quey
   */
  public Query create(Panel panel) {
    return create(panel, "default name", "default desc", QueryState.ACTIVE, true, true, null);
  }
  
  /**
   * Creates new query
   * 
   * @param panel panel
   * @param name name
   * @param description description
   * @param state state
   * @param visible visible
   * @param allowEditReply whether to allow editing replies
   * @param closes closes
   * @return created query
   */
  public Query create(Panel panel, String name, String description, QueryState state, Boolean visible, Boolean allowEditReply, OffsetDateTime closes) {
    Query query = new Query();
    query.setAllowEditReply(allowEditReply);
    query.setCloses(closes);
    query.setDescription(description);
    query.setName(name);
    query.setState(state);
    query.setVisible(visible);
    Query result = getApi().createQuery(panel.getId(), query);
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
  public Query findQuery(Long panelId, Long queryId) {
    return getApi().findQuery(panelId, queryId);
  }
  
  /**
   * Lists queries
   * 
   * @param panelId panel id
   * @return found queries
   * @throws ApiException 
   */
  public List<Query> listQueries(Long panelId) {
    return getApi().listQueries(panelId);
  }

  /**
   * Deletes a panel from the API
   * 
   * @param panel panel to be deleted
   * @param queryId query page id
   * @throws ApiException 
   */
  public void delete(Panel panel, Long queryId) {
    getApi().deleteQuery(panel.getId(), queryId);
    
    removeCloseable(closable -> {
      if (!(closable instanceof Panel)) {
        return false;
      }

      Panel closeablePanel = (Panel) closable;
      return closeablePanel.getId().equals(panel.getId());
    });
  }
  
  /**
   * Asserts query count within the system
   * 
   * @param panelId panel id
   * @param expected expected count
   * @throws ApiException 
   */
  public void assertCount(int expected, Long panelId) {
    assertEquals(expected, listQueries(panelId).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param panelId panel id
   * @param queryId query page id
   */
  public void assertFindFailStatus(int expectedStatus, Long panelId, Long queryId) {
    try {
      getApi().findQuery(panelId, queryId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {  
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts create status fails with given status code
   * 
   * @param panel panel
   * @param name name
   * @param description description
   * @param state state
   * @param visible visible
   * @param allowEditReply whether to allow editing replies
   * @param closes closes
   */
  public void assertCreateFailStatus(int expectedStatus, Panel panel, String name, String description, QueryState state, Boolean visible, Boolean allowEditReply, OffsetDateTime closes) {
    try {
      create(panel, name, description, state, visible, allowEditReply, closes);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
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
  public void assertDeleteFailStatus(int expectedStatus, Query body) {
    try {
      Long panelId = panelIds.get(body.getId());
      getApi().deleteQuery(panelId, body.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts list status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param panelId panel id
   */
  public void assertListFailStatus(int expectedStatus, Long panelId) {
    try {
      getApi().listQueries(panelId);
      fail(String.format("Expected list to fail with status %d", expectedStatus));
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
  public void clean(Query queryPageScale1d) {
    Long panelId = panelIds.get(queryPageScale1d.getId());
    getApi().deleteQuery(panelId, queryPageScale1d.getId());
  }

}
