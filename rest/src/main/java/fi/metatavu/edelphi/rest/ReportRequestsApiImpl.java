package fi.metatavu.edelphi.rest;

import java.util.List;
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
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.permissions.DelfoiActionName;
import fi.metatavu.edelphi.permissions.PermissionController;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.reports.text.batch.TextReportProperties;
import fi.metatavu.edelphi.rest.api.ReportRequestsApi;
import fi.metatavu.edelphi.rest.model.ReportRequest;
import fi.metatavu.edelphi.rest.model.ReportRequestOptions;

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
  private PermissionController permissionController;
  
  @Inject
  private PanelController panelController;

  @Inject
  private QueryController queryController;

  @Override
  @RolesAllowed("user")
  public Response createReportRequest(ReportRequest body) {
    Panel panel = panelController.findPanelById(body.getPanelId());
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }

    Query query = queryController.findQueryById(body.getQueryId());
    if (query == null || queryController.isQueryArchived(query)) {
      return createBadRequest(String.format("Invalid query id %d", body.getQueryId()));
    }
    
    ReportRequestOptions options = body.getOptions();
    List<Long> pageIds;
    
    if (options.getQueryPageIds() != null && !options.getQueryPageIds().isEmpty()) {
      pageIds = options.getQueryPageIds();
    } else {
      List<QueryPage> queryPages = queryController.listQueryPages(query, null);
      pageIds = queryPages.stream().map(QueryPage::getId).collect(Collectors.toList());
    }
    
    PanelStamp stamp = body.getStampId() == null ? panel.getCurrentStamp() : panelController.findPanelStampById(body.getStampId());
    if (stamp == null) {
      return createBadRequest(String.format("Invalid panel stamp %d", body.getStampId()));
    }
    
    // TODO: Permissions
    
    Properties properties = new Properties();
    properties.put(TextReportProperties.BASE_URL, getBaseUrl());
    properties.put(TextReportProperties.LOCALE, getLocale().toString());
    properties.put(TextReportProperties.PAGE_IDS, StringUtils.join(pageIds, ","));
    properties.put(TextReportProperties.STAMP_ID, stamp.getId().toString());
    
    if (options.getExpertiseGroupIds() != null) {
      properties.put(TextReportProperties.EXPERTISE_GROUP_IDS, StringUtils.join(options.getExpertiseGroupIds(), ","));
    }
    
    if (body.getDelivery() != null && StringUtils.isNotBlank(body.getDelivery().getEmail())) {
      properties.put(TextReportProperties.DELIVERY_EMAIL, body.getDelivery().getEmail());
    } else {
      properties.put(TextReportProperties.DELIVERY_EMAIL, getLoggedUser().getDefaultEmailAsString());      
    }
    
    JobOperator jobOperator = BatchRuntime.getJobOperator();
    jobOperator.start("htmlReportJob", properties);
    
    return Response.status(Status.ACCEPTED).build();
  }
}
