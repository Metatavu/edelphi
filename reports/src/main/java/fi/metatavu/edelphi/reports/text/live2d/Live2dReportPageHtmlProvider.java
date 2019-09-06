package fi.metatavu.edelphi.reports.text.live2d;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.queries.ScatterValue;
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.charts.ChartController;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;
import fi.metatavu.edelphi.reports.text.AbstractReportPageHtmlProvider;
import fi.metatavu.edelphi.reports.text.TextReportPageContext;

/**
 * Text report HTML provider for live2d reports
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class Live2dReportPageHtmlProvider extends AbstractReportPageHtmlProvider {

  private static final String OPTIONS_X = "live2d.options.x";
  private static final String OPTIONS_Y = "live2d.options.y";
  
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
  
  @Override
  public String getPageHtml(TextReportPageContext exportContext) throws ReportException {
    Locale locale = exportContext.getLocale();
    Long[] queryReplyIds = exportContext.getQueryReplyIds();
    Long[] commentCategoryIds = exportContext.getCommentCategoryIds();
    
    List<QueryReply> queryReplies = queryReplyIds == null ? Collections.emptyList() : Arrays.stream(queryReplyIds).map(queryReplyController::findQueryReply).collect(Collectors.toList());
    QueryPage queryPage = exportContext.getPage();
    PanelStamp panelStamp = exportContext.getStamp();
    return renderPage(locale, queryPage, panelStamp, queryReplies, commentCategoryIds);
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
  private String renderPage(Locale locale, QueryPage queryPage, PanelStamp panelStamp, List<QueryReply> queryReplies, Long[] commentCategoryIds) throws ReportException {
    try (InputStream htmlStream = getClass().getClassLoader().getResourceAsStream("report-contents.html")) {
      Document document = Jsoup.parse(htmlStream, "UTF-8", "");
      
      String labelX = queryPageController.getSetting(queryPage, "live2d.label.x");
      String labelY = queryPageController.getSetting(queryPage, "live2d.label.y");
      String thesis = queryPageController.getSetting(queryPage, "thesis.text");
      String description = queryPageController.getSetting(queryPage, "thesis.description");

      List<String> optionsX = queryPageController.getListSetting(queryPage, OPTIONS_X);
      List<String> optionsY = queryPageController.getListSetting(queryPage, OPTIONS_Y);
      
      List<ScatterValue> scatterValues = queryPageController.getLive2dScatterValues(queryPage, queryReplies);

      String chartHtml = chartController.createLive2dChart(locale, queryPage, queryReplies, scatterValues, labelX, labelY, optionsX, optionsY);
      
      document.getElementById("title").html(queryPage.getTitle());
      
      if (thesis != null) {
        document.getElementById("thesis").html(thesis);
      }
      
      document.getElementById("chart").html(chartHtml);
      
      if (description != null) {
        document.getElementById("description").html(description);
      }
      
      List<QueryQuestionCommentCategory> categories = queryPageController.listCommentCategoriesByPage(queryPage);
      if (categories.isEmpty()) {
        String title = reportMessages.getText(locale, "reports.page.commentsTitle");
        String comments = renderComments(locale, panelStamp, queryPage, scatterValues, labelX, labelY, optionsX, optionsY, commentCategoryIds);
        document.getElementById("comments").append(renderCommentList(title, comments));
      } else {
        List<QueryQuestionCommentCategory> filteredCategories = categories.stream().filter(category -> commentCategoryIds == null || ArrayUtils.contains(commentCategoryIds, category.getId())).collect(Collectors.toList());
        for (QueryQuestionCommentCategory category : filteredCategories) {
          String title = category.getName();
          String comments = renderComments(locale, panelStamp, queryPage, scatterValues, labelX, labelY, optionsX, optionsY, new Long[] { category.getId() });
          document.getElementById("comments").append(renderCommentList(title, comments));
        }
      }

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
  private String renderComments(Locale locale, PanelStamp panelStamp, QueryPage queryPage, List<ScatterValue> scatterValues, String labelX, String labelY, List<String> optionsX, List<String> optionsY, Long[] commentCategoryIds) throws ReportException {
    Map<Long, List<QueryQuestionComment>> childCommentMap = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    List<QueryQuestionComment> rootComments = filterComments(commentCategoryIds, listRootComments(panelStamp, queryPage));
    Map<Long, String> commentAnswers = getCommentAnswers(rootComments, scatterValues, labelX, labelY, optionsX, optionsY);
    
    rootComments.sort(new Comparator<QueryQuestionComment>() {

      @Override
      public int compare(QueryQuestionComment o1, QueryQuestionComment o2) {
        return commentAnswers.get(o1.getId()).compareTo(commentAnswers.get(o2.getId()));
      }
      
    });
    
    StringBuilder html = new StringBuilder();
    
    for (QueryQuestionComment rootComment : rootComments) {
      html.append(renderComment(locale, rootComment, childCommentMap, commentAnswers));
    }
    
    return html.toString();
  }
  
  /**
   * Returns map of comment answers
   * 
   * @param rootComments root comments
   * @param scatterValues scatter values
   * @param labelX label x
   * @param labelY label y
   * @param optionsX options x
   * @param optionsY options y
   * @return map of comment answers
   */
  private Map<Long, String> getCommentAnswers(List<QueryQuestionComment> rootComments, List<ScatterValue> scatterValues, String labelX, String labelY, List<String> optionsX, List<String> optionsY) {
    Map<Long, ScatterValue> valueMap = scatterValues.stream().collect(Collectors.toMap(ScatterValue::getReplyId, value -> value));
    return rootComments.stream().collect(Collectors.toMap(QueryQuestionComment::getId, rootComment -> getCommentLabel(rootComment, valueMap, labelX, labelY, optionsX, optionsY)));
  }

  /**
   * Returns comment answer
   * 
   * @param rootComment comment
   * @param valueMap value map
   * @param labelX label x
   * @param labelY label y
   * @param optionsX options x
   * @param optionsY options y
   * @return comment answer
   */
  private String getCommentLabel(QueryQuestionComment rootComment, Map<Long, ScatterValue> valueMap, String labelX, String labelY, List<String> optionsX,  List<String> optionsY) {
    ScatterValue value = valueMap.get(rootComment.getQueryReply().getId());
    if (value != null) {
      int x = (int) Math.round(value.getX());
      int y = (int) Math.round(value.getY());
      
      if (x < optionsX.size() && y < optionsY.size()) {
        return String.format("<b>%s</b>: %s<br/><br/><b>%s</b>: %s<br/><br/>", labelX, optionsX.get(x), labelY, optionsY.get(y));
      }
    }
  
    return "-";
  }
  
  /**
   * Filters out comments based on report settings
   * 
   * @param questionComments comments
   * @return filtered comments
   */
  private List<QueryQuestionComment> filterComments(Long[] commentCategoryIds, List<QueryQuestionComment> questionComments) {
    if (commentCategoryIds == null) {
      return questionComments;
    }
    
    return questionComments.stream()
      .filter(comment -> comment.getCategory() != null)
      .filter(comment -> ArrayUtils.contains(commentCategoryIds, comment.getCategory().getId()))
      .collect(Collectors.toList());
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
   * @param title title
   * @param comments comments HTML
   * @return comment list HTML
   * @throws ReportException
   */
  private String renderCommentList(String title, String comments) throws ReportException {
    try (InputStream htmlStream = getClass().getClassLoader().getResourceAsStream("report-comment-list.html")) {
      Document document = Jsoup.parse(htmlStream, "UTF-8", "");
      
      document.getElementById("comments-title").html(title);
      document.getElementById("comments").html(comments);

      document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);    
      document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);

      return document.body().outerHtml();
    } catch (IOException e) {
      throw new ReportException(e);
    }
  }
  
  /**
   * Renders a report comment 
   * 
   * @param comment comment
   * @param childComments child comment list
   * @return
   * @throws ReportException
   */
  private String renderComment(Locale locale, QueryQuestionComment comment, Map<Long, List<QueryQuestionComment>> childCommentMap, Map<Long, String> commentAnswers) throws ReportException {
    List<QueryQuestionComment> childComments = childCommentMap.get(comment.getId());
    String commentAnswer = commentAnswers.get(comment.getId());
    
    try (InputStream htmlStream = getClass().getClassLoader().getResourceAsStream("report-comment.html")) {
      Document document = Jsoup.parse(htmlStream, "UTF-8", "");
      
      document.getElementById("comment-text").html(comment.getComment());
      
      if (commentAnswer != null) {
        document.getElementById("comment-answer").html(commentAnswer);
      }
      
      document.getElementById("comment-date").html(reportMessages.getText(locale, "reports.page.commentDate", comment.getCreated()));
      
      if (childComments != null) {
        for (QueryQuestionComment childComment : childComments) {
          document.getElementById("comment-children").append(renderComment(locale, childComment, childCommentMap, commentAnswers)); 
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
