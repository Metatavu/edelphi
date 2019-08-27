package fi.metatavu.edelphi.reports.text.live2d;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryNumericFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.charts.ChartController;
import fi.metatavu.edelphi.reports.charts.ScatterValue;
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
  private static final String FIELD_NAME_X = "x";
  private static final String FIELD_NAME_Y = "y";
  
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
  private QueryNumericFieldDAO queryNumericFieldDAO;

  @Inject
  private QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO;
  
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
      
      String labelX = queryPageController.getSetting(queryPage, "live2d.label.x");
      String labelY = queryPageController.getSetting(queryPage, "live2d.label.y");
      String thesis = queryPageController.getSetting(queryPage, "thesis.text");
      String description = queryPageController.getSetting(queryPage, "thesis.description");
      
      QueryNumericField queryFieldX = queryNumericFieldDAO.findByQueryPageAndName(queryPage, FIELD_NAME_X);
      QueryNumericField queryFieldY = queryNumericFieldDAO.findByQueryPageAndName(queryPage, FIELD_NAME_Y);

      List<String> optionsX = queryPageController.getListSetting(queryPage, OPTIONS_X);
      List<String> optionsY = queryPageController.getListSetting(queryPage, OPTIONS_Y);
      
      List<QueryQuestionNumericAnswer> answersX = queryQuestionNumericAnswerDAO.listByQueryFieldAndRepliesIn(queryFieldX, queryReplies);
      List<QueryQuestionNumericAnswer> answersY = queryQuestionNumericAnswerDAO.listByQueryFieldAndRepliesIn(queryFieldY, queryReplies);
      
      Map<Long, Double> answerMapX = answersX.stream().collect(Collectors.toMap(answer -> answer.getQueryReply().getId(), QueryQuestionNumericAnswer::getData));
      Map<Long, Double> answerMapY = answersY.stream().collect(Collectors.toMap(answer -> answer.getQueryReply().getId(), QueryQuestionNumericAnswer::getData));
      Map<Long, Double[]> answerMap = queryReplies.stream().map(QueryReply::getId).collect(Collectors.toMap(queryReplyId -> queryReplyId, queryReplyId -> new Double[] { answerMapX.get(queryReplyId), answerMapY.get(queryReplyId) }));
      
      List<ScatterValue> scatterValues = new ArrayList<>();
      for (Map.Entry<Long, Double[]> answerMapEntry : answerMap.entrySet()) {
        Double[] values = answerMapEntry.getValue();
        if (values[0] != null && values[1] != null) {
          scatterValues.add(new ScatterValue(answerMapEntry.getKey(), values[0], values[1]));
        }
      }
      
      String chartHtml = chartController.createLive2dChart(locale, queryPage, queryReplies, scatterValues, labelX, labelY, optionsX, optionsY);
      
      document.getElementById("title").html(queryPage.getTitle());
      
      if (thesis != null) {
        document.getElementById("thesis").html(thesis);
      }
      
      document.getElementById("chart").html(chartHtml);
      
      if (description != null) {
        document.getElementById("description").html(description);
      }
      
      document.getElementById("comments-title").html(reportMessages.getText(locale, "reports.page.commentsTitle"));
      document.getElementById("comments").html(renderComments(panelStamp, queryPage, scatterValues, labelX, labelY, optionsX, optionsY));
      
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
  private String renderComments(PanelStamp panelStamp, QueryPage queryPage, List<ScatterValue> scatterValues, String labelX, String labelY, List<String> optionsX, List<String> optionsY) throws ReportException {
    Map<Long, List<QueryQuestionComment>> childCommentMap = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    List<QueryQuestionComment> rootComments = listRootComments(panelStamp, queryPage);
    Map<Long, String> commentAnswers = getCommentAnswers(rootComments, scatterValues, labelX, labelY, optionsX, optionsY);
    
    rootComments.sort(new Comparator<QueryQuestionComment>() {

      @Override
      public int compare(QueryQuestionComment o1, QueryQuestionComment o2) {
        return commentAnswers.get(o1.getId()).compareTo(commentAnswers.get(o2.getId()));
      }
      
    });
    
    StringBuilder html = new StringBuilder();
    
    for (QueryQuestionComment rootComment : rootComments) {
      html.append(renderComment(rootComment, childCommentMap, commentAnswers));
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
  private String renderComment(QueryQuestionComment comment, Map<Long, List<QueryQuestionComment>> childCommentMap, Map<Long, String> commentAnswers) throws ReportException {
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
          document.getElementById("comment-children").append(renderComment(childComment, childCommentMap, commentAnswers)); 
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
