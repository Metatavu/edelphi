package fi.metatavu.edelphi.rest.test.functional.builder.impl;

import java.util.HashMap;
import java.util.Map;

import org.openapitools.client.api.QuerySectionsApi;
import org.openapitools.client.model.Panel;
import org.openapitools.client.model.Query;
import org.openapitools.client.model.QuerySection;

import fi.metatavu.edelphi.rest.client.ApiClient;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;

/**
 * Test builder resource for panels
 * 
 * @author Antti Leppä
 */
public class QuerySectionTestBuilderResource extends ApiTestBuilderResource<QuerySection, QuerySectionsApi> {
  
  private Map<Long, Long> panelIds = new HashMap<>();
  private Map<Long, Long> queryIds = new HashMap<>();
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public QuerySectionTestBuilderResource(AbstractTestBuilder<ApiClient> testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /**
   * Creates new query section with default values
   * 
   * @param panel panel
   * @param query query
   * @return created query section
   * @throws ApiException 
   */
  public QuerySection create(Panel panel, Query query) {
    return create(panel, query, "default title", 1, true, true, true);
  }

  /**
   * Creates new query section with default values
   * 
   * @param panel panel
   * @param query query
   * @param title title
   * @param sectionNumber section number
   * @param commentable commentable
   * @param viewDiscussions view discussions
   * @param visible visible
   * @return created query section
   * @throws ApiException 
   */
  public QuerySection create(Panel panel, Query query, String title, Integer sectionNumber, Boolean commentable, Boolean viewDiscussions, Boolean visible) {
    QuerySection querySection = new QuerySection();
    querySection.setCommentable(commentable);
    querySection.setSectionNumber(sectionNumber);
    querySection.setTitle(title);
    querySection.setViewDiscussions(viewDiscussions);
    querySection.setVisible(visible);
    QuerySection result = getApi().createQuerySection(panel.getId(), query.getId(), querySection);
    panelIds.put(result.getId(), panel.getId());
    queryIds.put(result.getId(), query.getId());
    return addClosable(result);
  }
  
  /**
   * Deletes a panel from the API
   * 
   * @param panel panel to be deleted
   * @param querySectionId query page id
   * @throws ApiException 
   */
  public void delete(Panel panel, Query query, Long querySectionId) {
    getApi().deleteQuerySection(panel.getId(), query.getId(), querySectionId);
    
    removeCloseable(closable -> {
      if (!(closable instanceof Panel)) {
        return false;
      }

      Panel closeablePanel = (Panel) closable;
      return closeablePanel.getId().equals(panel.getId());
    });
  }

  @Override
  public void clean(QuerySection querySection) {
    Long panelId = panelIds.get(querySection.getId());
    Long queryId = queryIds.get(querySection.getId());
    getApi().deleteQuerySection(panelId, queryId, querySection.getId());
  }

}
