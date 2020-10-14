package fi.metatavu.edelphi.reports.text.batch;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemProcessor;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.text.TextReportController;
import fi.metatavu.edelphi.reports.text.TextReportPageContext;

/**
 * Generates HTML reports for given query pages
 * 
 * @author Antti Leppä
 */
@Named
public class TextReportPageHtmlProcessor extends TypedItemProcessor<QueryPage, String> {

  @Inject
  private Logger logger;

  @Inject
  private PanelController panelController;

  @Inject
  private TextReportController htmlReportController;
  
  @Inject
  @JobProperty
  private String baseUrl;
  
  @Inject
  @JobProperty
  private Locale locale;

  @Inject
  @JobProperty
  private Long stampId;

  @Inject
  @JobProperty
  private Long[] expertiseGroupIds;

  @Inject
  @JobProperty
  private Long[] panelUserGroupIds;
  
  @Inject
  @JobProperty
  private Long[] queryReplyIds;

  @Inject
  @JobProperty
  private Long[] commentCategoryIds;
  
  @Override
  public String process(QueryPage queryPage) throws Exception {
    PanelStamp stamp = panelController.findPanelStampById(stampId);
    if (stamp == null) {
      throw new ReportException(String.format("Could not find panel stamp %d", stampId));
    }
    
    logger.info("Processing query page {}", queryPage.getId());

    return htmlReportController.getPageHtml(new TextReportPageContext(baseUrl, locale, stamp, panelUserGroupIds, expertiseGroupIds, queryReplyIds, commentCategoryIds, queryPage));
  }

}
