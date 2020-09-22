package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;
import fi.metatavu.edelphi.reports.spreadsheet.comments.GenericReportPageCommentProcessor;
import fi.metatavu.edelphi.reports.spreadsheet.comments.ReportPageCommentProcessor;

public abstract class AbstractQueryPageSpreadsheetExporter implements QueryPageSpreadsheetExporter {

  @Inject
  private Logger logger;

  @Inject
  private ReportMessages reportMessages;
  
  @Inject
  private QueryPageController queryPageController;

  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;
  
  @Override
  public ReportPageCommentProcessor exportComments(QueryPage queryPage, PanelStamp stamp, List<QueryReply> replies) {
    return new GenericReportPageCommentProcessor(queryPage, listRootComments(stamp, queryPage), new HashMap<>());
  }
  
  @Override
  public void exportRootComments(SpreadsheetExportContext exportContext) {
    QueryPage queryPage = exportContext.getQueryPage();    
    if (!isPageCommentable(queryPage)) {
      return;
    }
    
    List<QueryReply> queryReplies = exportContext.getQueryReplies();    
    List<QueryQuestionCommentCategory> categories = listPageCommentCategories(queryPage);
    Map<Long, Integer> commentColumns = addCommentColums(exportContext, categories);
    exportQueryPageComments(exportContext, categories, commentColumns, queryReplies);
  }
  
  /**
   * Lists page's root comments
   * 
   * @return page's root comments
   */
  protected List<QueryQuestionComment> listRootComments(PanelStamp panelStamp, QueryPage queryPage) {
    return queryQuestionCommentDAO.listRootCommentsByQueryPageAndStampOrderByCreated(queryPage, panelStamp); 
  } 

  /**
   * Exports query page comments
   * 
   * @param exportContext spreadsheet export context
   * @param categories comment category list
   * @param commentColumns category id > column index map
   * @param queryReplies query replies
   */
  private void exportQueryPageComments(SpreadsheetExportContext exportContext, List<QueryQuestionCommentCategory> categories, Map<Long, Integer> commentColumns, List<QueryReply> queryReplies) {
    for (QueryReply queryReply : queryReplies) {
      exportQueryReplyComments(exportContext, categories, commentColumns, queryReply);
    }
  }

  /**
   * Returns whether query page is commentable or not
   * 
   * @param queryPage query page
   * @return whether query page is commentable or not
   */
  private boolean isPageCommentable(QueryPage queryPage) {
    if (queryPageController.getBooleanSetting(queryPage, "thesis.commentable")) {
      return true;
    }
          
    return queryPageController.getBooleanSetting(queryPage, "text.commentable");
  }

  /**
   * Lists all comment categories available on page (including query wide and page specific categories)
   * 
   * @param queryPage query page
   * @return comment categories available on page
   */
  private List<QueryQuestionCommentCategory> listPageCommentCategories(QueryPage queryPage) {
    return queryPageController.listCommentCategoriesByPage(queryPage, true);
  }
  
  /**
   * Adds comment columns into exported spreadsheet and returns indices as category id > column index map. Non categorized column in stored with key 0l
   * 
   * @param exportContext spreadsheet export context
   * @param categories comment category list
   * @return category id > column index map
   */
  private Map<Long, Integer> addCommentColums(SpreadsheetExportContext exportContext, List<QueryQuestionCommentCategory> categories) {
    HashMap<Long, Integer> result = new HashMap<>();
    QueryPage queryPage = exportContext.getQueryPage();
    Locale locale = exportContext.getLocale();
    
    if (categories.isEmpty()) {
      String commentsLabel = reportMessages.getText(locale, "reports.spreadsheet.comment");
      String columnTitle = String.format("%s/%s", queryPage.getTitle(), commentsLabel);
      Integer columnIndex = exportContext.addColumn(columnTitle);
      result.put(0l, columnIndex);
    } else {
      List<QueryQuestionCommentCategory> filteredCategories = getFilteredCommentCategories(exportContext, categories);
      for (QueryQuestionCommentCategory category : filteredCategories) {
        String categoryTitle = category.getName();
        String columnTitle = String.format("%s/%s", queryPage.getTitle(), categoryTitle);        
        int columnIndex = exportContext.addColumn(columnTitle);
        result.put(category.getId(), columnIndex);
      }      
    }
    
    return result;
  }
  
  /**
   * Exports query page comments for given reply 
   * 
   * @param exportContext spreadsheet export context
   * @param categories comment category list
   * @param commentColumns category id > column index map
   * @param queryReply query reply
   */
  private void exportQueryReplyComments(SpreadsheetExportContext exportContext, List<QueryQuestionCommentCategory> categories, Map<Long, Integer> commentColumns, QueryReply queryReply) {
    QueryPage queryPage = exportContext.getQueryPage();
    
    if (categories.isEmpty()) {
      Integer columnIndex = commentColumns.get(0l);
      if (columnIndex == null) {
        logger.error("Failed to resolve comment column for non categorized comment");
      } else {
        QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
        exportContext.setCellValue(queryReply, columnIndex, comment != null ? comment.getComment() : null);
      }
    } else {
      List<QueryQuestionCommentCategory> filteredCategories = getFilteredCommentCategories(exportContext, categories);
      for (QueryQuestionCommentCategory category : filteredCategories) {
        Integer columnIndex = commentColumns.get(category.getId());
        if (columnIndex == null) {
          logger.error("Failed to resolve comment column for category {} comment", category.getId());
        } else {
          QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyQueryPageAndCategory(queryReply, queryPage, category);
          exportContext.setCellValue(queryReply, columnIndex, comment != null ? comment.getComment() : null);
        }
      }
    }
  }

  /**
   * Adds 
   * 
   * @param exportContext
   * @param categories
   * @return
   */
  private List<QueryQuestionCommentCategory> getFilteredCommentCategories(SpreadsheetExportContext exportContext, List<QueryQuestionCommentCategory> categories) {
    Long[] commentCategoryIds = exportContext.getCommentCategoryIds();
    List<QueryQuestionCommentCategory> filteredCategories = categories.stream().filter(category -> commentCategoryIds == null || ArrayUtils.contains(commentCategoryIds, category.getId())).collect(Collectors.toList());
    return filteredCategories;
  }
  

}
