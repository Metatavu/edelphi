package fi.metatavu.edelphi.reports.text.live2d;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.charts.ChartController;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;
import fi.metatavu.edelphi.reports.spreadsheet.comments.GenericReportPageCommentProcessor;
import fi.metatavu.edelphi.reports.spreadsheet.comments.ReportPageCommentProcessor;
import fi.metatavu.edelphi.reports.text.AbstractReportPageHtmlProvider;
import fi.metatavu.edelphi.reports.text.TextReportPageContext;

/**
 * Text report HTML provider for live2d reports
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class Live2dReportPageHtmlProvider extends AbstractReportPageHtmlProvider {

  @Inject
  private ReportMessages reportMessages;

  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;
  
  @Inject
  private ChartController chartController;

  @Inject
  private QueryReplyController queryReplyController;

  @Inject
  private QueryPageController queryPageController;

  @Inject
  @JobProperty
  private Locale locale;
  
  @Inject
  @JobProperty
  private Long[] queryReplyIds;
  
  @Override
  public String getPageHtml(TextReportPageContext exportContext) throws ReportException {
    List<QueryReply> queryReplies = queryReplyIds == null ? Collections.emptyList() : Arrays.stream(queryReplyIds).map(queryReplyController::findQueryReply).collect(Collectors.toList());
    QueryPage queryPage = exportContext.getPage();
    PanelStamp panelStamp = exportContext.getStamp();
    return renderPage(queryPage, panelStamp, queryReplies);
  }
  
  /**
   * Renders a report page
   * 
   * @param queryPage query page
   * @param panelStamp stamp
   * @param queryReplies query replies
   * @return rendered page
   * @throws ReportException thrown when generation fails
   */
  private String renderPage(QueryPage queryPage, PanelStamp panelStamp, List<QueryReply> queryReplies) throws ReportException {
    try (InputStream htmlStream = getClass().getClassLoader().getResourceAsStream("report-contents.html")) {
      Document document = Jsoup.parse(htmlStream, "UTF-8", "");
      
      String fieldNameX = "x";
      String fieldNameY = "y";
      String labelX = queryPageController.getSetting(queryPage, "live2d.label.x");
      String labelY = queryPageController.getSetting(queryPage, "live2d.label.y");
      String thesis = queryPageController.getSetting(queryPage, "thesis.text");
      String description = queryPageController.getSetting(queryPage, "thesis.description");
      
      String chartHtml = chartController.createLive2dChart(locale, queryPage, queryReplies, queryPage.getTitle(), labelX, labelY, fieldNameX, fieldNameY);
      
      document.getElementById("title").html(queryPage.getTitle());
      
      if (thesis != null) {
        document.getElementById("thesis").html(thesis);
      }
      
      document.getElementById("chart").html(chartHtml);
      
      if (description != null) {
        document.getElementById("description").html(description);
      }
      
      document.getElementById("comments-title").html(reportMessages.getText(locale, "reports.page.commentsTitle"));
      document.getElementById("comments").html(renderComments(panelStamp, queryPage));
      
      document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);    
      document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);

      return document.body().outerHtml();
    } catch (IOException e) {
      throw new ReportException(e);
    }
  }
  
  /**
   * Renders reports
   * 
   * @param panelStamp stamp
   * @param queryPage page
   * @return report comments
   * @throws ReportException thrown when generation fails
   */
  private String renderComments(PanelStamp panelStamp, QueryPage queryPage) throws ReportException {
    Map<Long, List<QueryQuestionComment>> childCommentMap = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    List<QueryQuestionComment> rootComments = listRootComments(panelStamp, queryPage);

    // TODO: Answers
    ReportPageCommentProcessor sorter = new GenericReportPageCommentProcessor(queryPage, rootComments, new HashMap<>());
    sorter.processComments();
    
    StringBuilder html = new StringBuilder();
    
    for (QueryQuestionComment rootComment : sorter.getRootComments()) {
      html.append(renderComment(rootComment, childCommentMap));
    }
    
    return html.toString();
  }
  
  /**
   * Lists page's root comments
   * 
   * @return page's root comments
   */
  private List<QueryQuestionComment> listRootComments(PanelStamp panelStamp, QueryPage queryPage) {
    return queryQuestionCommentDAO.listRootCommentsByQueryPageAndStampOrderByCreated(queryPage, panelStamp); 
  }
  
  /**
   * Renders a report comment 
   * 
   * @param comment comment
   * @param childComments child comment list
   * @return
   * @throws ReportException
   */
  private String renderComment(QueryQuestionComment comment, Map<Long, List<QueryQuestionComment>> childCommentMap) throws ReportException {
    List<QueryQuestionComment> childComments = childCommentMap.get(comment.getId());
    
    try (InputStream htmlStream = getClass().getClassLoader().getResourceAsStream("report-comment.html")) {
      Document document = Jsoup.parse(htmlStream, "UTF-8", "");
      
      document.getElementById("comment-text").html(comment.getComment());
      document.getElementById("comment-date").html(reportMessages.getText(locale, "reports.page.commentDate", comment.getCreated()));
      
      if (childComments != null) {
        for (QueryQuestionComment childComment : childComments) {
          document.getElementById("comment-children").append(renderComment(childComment, childCommentMap)); 
        }
      }
      
      document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);    
      document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);

      return document.body().outerHtml();
    } catch (IOException e) {
      throw new ReportException(e);
    }
  }
}
