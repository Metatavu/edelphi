package fi.metatavu.edelphi.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.jboss.ejb3.annotation.SecurityDomain;

import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.permissions.DelfoiActionName;
import fi.metatavu.edelphi.permissions.PermissionController;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.reports.batch.ReportBatchProperties;
import fi.metatavu.edelphi.rest.api.ReportRequestsApi;
import fi.metatavu.edelphi.rest.model.ReportFormat;
import fi.metatavu.edelphi.rest.model.ReportRequest;
import fi.metatavu.edelphi.rest.model.ReportRequestOptions;
import fi.metatavu.edelphi.rest.model.ReportType;

import org.slf4j.Logger;

/**
 * Report requests API implementation
 * 
 * @author Antti Leppä
 */
@RequestScoped
@Stateful
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
@SecurityDomain("keycloak")
public class ReportRequestsApiImpl extends AbstractApi implements ReportRequestsApi {

  @Inject
  private Logger logger;

  @Inject
  private PermissionController permissionController;
  
  @Inject
  private PanelController panelController;

  @Inject
  private QueryController queryController;

  @Inject
  private QueryReplyController queryReplyController;

  @Inject
  private QueryPageController queryPageController;

  @Override
  @RolesAllowed("user")
  public Response createReportRequest(ReportRequest body) {
    Panel panel = panelController.findPanelById(body.getPanelId());
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.MANAGE_PANEL)) {
      return createForbidden("Forbidden");
    }

    logger.info("Requested a report with locale {}", getLocale());

    Query query = queryController.findQueryById(body.getQueryId());
    if (query == null || queryController.isQueryArchived(query)) {
      return createBadRequest(String.format("Invalid query id %d", body.getQueryId()));
    }
    
    if (!queryController.isPanelsQuery(query, panel)) {
      return createBadRequest(String.format("Query %d is not from panel %d", body.getQueryId(), body.getPanelId()));
    }
    
    ReportRequestOptions options = body.getOptions();
    List<Long> queryPageIds;
    
    if (options.getQueryPageIds() != null && !options.getQueryPageIds().isEmpty()) {
      queryPageIds = options.getQueryPageIds();
      Map<Long, QueryPage> pages = new HashMap<>();
      
      for (int i = 0; i < queryPageIds.size(); i++) {
        Long queryPageId = queryPageIds.get(i);
        QueryPage queryPage = queryPageController.findQueryPage(queryPageId);
        
        if (queryPage == null || queryPageController.isQueryPageArchived(queryPage)) {
          return createBadRequest(String.format("Invalid query page id %d", queryPageId));
        }
        
        if (!queryPageController.isQuerysPage(query, queryPage)) {
          return createBadRequest(String.format("Query page %d is not from query", queryPageId, query.getId()));
        }
        
        pages.put(queryPageId, queryPage);        
      }
      
      queryPageIds = queryPageIds.stream().filter(queryPageId -> {
        QueryPage queryPage = pages.get(queryPageId);
        return queryPageController.isQueryPageVisible(queryPage);
      }).collect(Collectors.toList());
    } else {
      List<QueryPage> queryPages = queryController.listQueryPages(query, true);
      queryPageIds = queryPages.stream().map(QueryPage::getId).collect(Collectors.toList());
    }
    
    PanelStamp stamp = body.getStampId() == null ? panel.getCurrentStamp() : panelController.findPanelStampById(body.getStampId());
    if (stamp == null || panelController.isPanelStampArchived(stamp)) {
      return createBadRequest(String.format("Invalid panel stamp %d", body.getStampId()));
    }
    
    if (!panelController.isPanelsStamp(panel, stamp)) {
      return createBadRequest(String.format("Stamp %d is not from panel %d", stamp.getId(), panel.getId()));
    }
    
    Properties properties = new Properties();
    properties.put(ReportBatchProperties.QUERY_ID, query.getId().toString());
    properties.put(ReportBatchProperties.BASE_URL, getBaseUrl());
    properties.put(ReportBatchProperties.LOCALE, getLocale().toString());
    properties.put(ReportBatchProperties.PAGE_IDS, StringUtils.join(queryPageIds, ","));
    properties.put(ReportBatchProperties.STAMP_ID, stamp.getId().toString());
    
    List<PanelUserExpertiseGroup> expertiseGroups = null;
    if (options.getExpertiseGroupIds() != null && !options.getExpertiseGroupIds().isEmpty()) {
      expertiseGroups = options.getExpertiseGroupIds().stream().map(panelController::findPanelUserExpertiseGroup).collect(Collectors.toList());
      if (expertiseGroups.contains(null)) {
        return createBadRequest("Invalid expertise group id");
      }
      
      properties.put(ReportBatchProperties.EXPERTISE_GROUP_IDS, StringUtils.join(options.getExpertiseGroupIds(), ","));
    }

    List<PanelUserGroup> panelUserGroups = null;
    if (options.getPanelUserGroupIds() != null && !options.getPanelUserGroupIds().isEmpty()) {
      panelUserGroups = options.getPanelUserGroupIds().stream().map(panelController::findPanelUserGroup).collect(Collectors.toList());
      if (panelUserGroups.contains(null)) {
        return createBadRequest("Invalid user group id");
      }
      
      properties.put(ReportBatchProperties.PANEL_USER_GROUP_IDS, StringUtils.join(options.getPanelUserGroupIds(), ","));
    }
    
    if (options.getCommentCategoryIds() != null && !options.getCommentCategoryIds().isEmpty()) {
      properties.put(ReportBatchProperties.COMMENT_CATEGORY_IDS, StringUtils.join(options.getCommentCategoryIds(), ","));
    }
    
    if (body.getDelivery() != null && StringUtils.isNotBlank(body.getDelivery().getEmail())) {
      properties.put(ReportBatchProperties.DELIVERY_EMAIL, body.getDelivery().getEmail());
    } else {
      properties.put(ReportBatchProperties.DELIVERY_EMAIL, getLoggedUser().getDefaultEmailAsString());      
    }

    List<QueryReply> replies = queryReplyController.listQueryReplies(query, stamp);
    if (expertiseGroups != null) {
      replies = queryReplyController.filterQueryRepliesByExpertiseGroup(replies, expertiseGroups);
    }
    
    if (panelUserGroups != null) {
      replies = queryReplyController.filterQueryRepliesByPanelUserGroup(replies, panelUserGroups);
    }
    
    properties.put(ReportBatchProperties.QUERY_REPLY_IDS, replies.stream().map(QueryReply::getId).map(String::valueOf).collect(Collectors.joining(",")));
    long jobId = requestReport(body.getType(), body.getFormat(), properties);
    if (jobId > 0) {
      return Response.status(Status.ACCEPTED).build();
    }
    
    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to submit job").build();
  }

  /**
   * Requests a report
   * 
   * @param reportType report type
   * @param properties properties
   * @return job id
   */
  private long requestReport(ReportType reportType, ReportFormat reportFormat, Properties properties) {
    switch (reportType) {
      case SPREADSHEET:
        return requestSpreadsheetReport(reportFormat, properties);
      case TEXT:
        return requestTextReport(reportFormat, properties);
      case IMAGES:
        return requestImageReport(reportFormat, properties);
    }
    
    return 0;
  }

  /**
   * Requests a spreadsheet report
   * 
   * @param properties properties
   * @return job id
   */
  private long requestSpreadsheetReport(ReportFormat reportFormat, Properties properties) {
    JobOperator jobOperator = BatchRuntime.getJobOperator();
    
    switch (reportFormat) {
      case CSV:
        return jobOperator.start("spreadsheetReportCsvJob", properties);
      case GOOGLE_SHEET:
        return jobOperator.start("spreadsheetReportGoogleSheetsJob", properties);
      default:
      break;
    }
    
    return 0;
  }

  /**
   * Requests a text report
   * 
   * @param properties properties
   * @return job id
   */
  private long requestTextReport(ReportFormat reportFormat, Properties properties) {
    JobOperator jobOperator = BatchRuntime.getJobOperator();
    
    switch (reportFormat) {
      case PDF:
        return jobOperator.start("textReportPdfJob", properties);
      case GOOGLE_DOCUMENT:
        return jobOperator.start("textReportGoogleDocumentJob", properties);
      default:
      break;
    }

    return 0;
  }

  /**
   * Requests an image report
   * 
   * @param properties properties
   * @return job id
   */
  private long requestImageReport(ReportFormat reportFormat, Properties properties) {
    JobOperator jobOperator = BatchRuntime.getJobOperator();
    
    switch (reportFormat) {
      case PNG:
        return jobOperator.start("imageReportPngJob", properties);
      default:
      break;
    }

    return 0;
  }
  
}
