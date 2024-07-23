package fi.metatavu.edelphi.reports.text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.charts.ChartController;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;

/**
 * Text report HTML provider for multiple 2d scale reports
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class Multiple1dScalesReportPageHtmlProvider extends AbstractReportPageHtmlProvider {
  
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
      
      String description = queryPageController.getSetting(queryPage, QueryPageController.THESIS_DESCRIPTION_OPTION);
      
      String label = queryPageController.getSetting(queryPage, QueryPageController.MULTIPLE_1D_SCALES_LABEL_OPTION);
      List<String> theses = queryPageController.getListSetting(queryPage, QueryPageController.MULTIPLE_1D_SCALES_THESES_OPTION);
      List<String> options = queryPageController.getListSetting(queryPage, QueryPageController.MULTIPLE_1D_SCALES_OPTIONS_OPTION);
         
      document.getElementById("title").html(queryPage.getTitle());
      
      double[][] pageValues = queryPageController.getMultipleScale1dValues(queryPage, queryReplies);
      for (int thesisIndex = 0; thesisIndex < pageValues.length; thesisIndex++) {
        String thesis = theses.get(thesisIndex);
        String chartTitle = StringUtils.isNotBlank(thesis) ? thesis : queryPage.getTitle();
        double[] thesisValues = pageValues[thesisIndex];
        String chartHtml = chartController.printGraphPNG(chartController.createBarChart(
                locale,
                chartTitle,
                getFieldLabel(thesis, label),
                options,
                thesisValues)
        );

        document.getElementById("chart").append(chartHtml);
      }
      
      if (description != null) {
        document.getElementById("description").html(description);
      }
      
      List<QueryQuestionCommentCategory> categories = queryPageController.listCommentCategoriesByPage(queryPage, true);
      if (categories.isEmpty()) {
        String title = reportMessages.getText(locale, "reports.page.commentsTitle");
        String comments = renderComments(locale, panelStamp, queryPage, commentCategoryIds);
        document.getElementById("comments").append(renderCommentList(title, comments));
      } else {
        List<QueryQuestionCommentCategory> filteredCategories = categories.stream().filter(category -> commentCategoryIds == null || ArrayUtils.contains(commentCategoryIds, category.getId())).collect(Collectors.toList());
        for (QueryQuestionCommentCategory category : filteredCategories) {
          String title = category.getName();
          String comments = renderComments(locale, panelStamp, queryPage, new Long[] { category.getId() });
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
  private String renderComments(Locale locale, PanelStamp panelStamp, QueryPage queryPage, Long[] commentCategoryIds) throws ReportException {
    Map<Long, List<QueryQuestionComment>> childCommentMap = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    List<QueryQuestionComment> rootComments = filterComments(commentCategoryIds, listRootComments(panelStamp, queryPage));
    
    StringBuilder html = new StringBuilder();
    
    for (QueryQuestionComment rootComment : rootComments) {
      html.append(renderComment(locale, rootComment, childCommentMap));
    }
    
    return html.toString();
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
  private String renderComment(Locale locale, QueryQuestionComment comment, Map<Long, List<QueryQuestionComment>> childCommentMap) throws ReportException {
    List<QueryQuestionComment> childComments = childCommentMap.get(comment.getId());
    
    try (InputStream htmlStream = getClass().getClassLoader().getResourceAsStream("report-comment.html")) {
      Document document = Jsoup.parse(htmlStream, "UTF-8", "");
      
      document.getElementById("comment-text").html(comment.getComment());
      document.getElementById("comment-date").html(reportMessages.getText(locale, "reports.page.commentDate", comment.getCreated()));
      
      if (childComments != null) {
        for (QueryQuestionComment childComment : childComments) {
          document.getElementById("comment-children").append(renderComment(locale, childComment, childCommentMap)); 
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
   * Returns field label for given thesis and label
   * 
   * @param thesis thesis
   * @param label label
   * @return field label
   */
  private String getFieldLabel(String thesis, String label) {
    return String.format("%s/%s", thesis, label);
  }
}
