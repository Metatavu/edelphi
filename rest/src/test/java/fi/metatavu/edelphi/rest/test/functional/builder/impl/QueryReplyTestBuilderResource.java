package fi.metatavu.edelphi.rest.test.functional.builder.impl;

import java.util.HashMap;
import java.util.Map;

import org.openapitools.client.api.QueryRepliesApi;
import org.openapitools.client.model.Panel;
import org.openapitools.client.model.Query;
import org.openapitools.client.model.QueryReply;

import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.rest.client.ApiClient;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;

/**
 * Test builder resource for panels
 * 
 * @author Antti Leppä
 */
public class QueryReplyTestBuilderResource extends ApiTestBuilderResource<QueryReply, QueryRepliesApi> {
  
  private Map<Long, Long> panelIds = new HashMap<>();
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public QueryReplyTestBuilderResource(AbstractTestBuilder<ApiClient> testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /**
   * Creates new query reply with default values
   * 
   * @param panel panel
   * @param query query
   * @return created query section
   * @throws ApiException 
   */
  public QueryReply create(Panel panel, Query query) {
    return create(panel, query, null);
  }

  /**
   * Creates new query reply
   * 
   * @param panel panel
   * @param query query
   * @param stamp stamp
   * @throws ApiException 
   */
  public QueryReply create(Panel panel, Query query, PanelStamp stamp) {
    QueryReply queryReply = new QueryReply();
    queryReply.setQueryId(query.getId());
    queryReply.setStampId(stamp != null ? stamp.getId() : null);
    QueryReply result = getApi().createQueryReply(panel.getId(),queryReply);
    panelIds.put(result.getId(), panel.getId());
    return addClosable(result);
  }
  
  /**
   * Deletes query reply
   * 
   * @param panel panel to be deleted
   * @param queryReplyId query page id
   * @throws ApiException 
   */
  public void delete(Panel panel, Query query, Long queryReplyId) {
    getApi().deleteQueryReply(panel.getId(), queryReplyId);
    
    removeCloseable(closable -> {
      if (!(closable instanceof Panel)) {
        return false;
      }

      Panel closeablePanel = (Panel) closable;
      return closeablePanel.getId().equals(panel.getId());
    });
  }

  @Override
  public void clean(QueryReply queryReply) {
    Long panelId = panelIds.get(queryReply.getId());
    getApi().deleteQueryReply(panelId, queryReply.getId());
  }

}
