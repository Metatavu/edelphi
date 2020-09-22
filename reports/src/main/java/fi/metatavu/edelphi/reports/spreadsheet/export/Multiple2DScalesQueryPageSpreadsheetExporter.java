package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;

@ApplicationScoped
public class Multiple2DScalesQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {
  
  private static final String THESES_OPTION = "multiple2dscales.theses";

  @Inject
  private QueryPageController queryPageController;

  @Inject
  private QueryFieldDAO queryFieldDAO;
    
  @Inject
  private QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO;

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();
  
    List<String> theses = queryPageController.getListSetting(queryPage, THESES_OPTION);
    for (int thesisIndex = 0, thesisCount = theses.size(); thesisIndex < thesisCount; thesisIndex++) {
      String fieldNameX = getFieldName(thesisIndex, "x");
      String fieldNameY = getFieldName(thesisIndex, "y");
      
      QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
      QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

      int columnIndexX = exportContext.addColumn(getColumnLabel(queryPage.getTitle(), queryFieldX.getCaption()));
      int columnIndexY = exportContext.addColumn(getColumnLabel(queryPage.getTitle(), queryFieldY.getCaption()));
      
      for (QueryReply queryReply : queryReplies) {
        QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX);
        QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);

        exportContext.setCellValue(queryReply, columnIndexX, answerX != null ? answerX.getOption().getText() : null);
        exportContext.setCellValue(queryReply, columnIndexY, answerY != null ? answerY.getOption().getText() : null);
      }
    }
  }

  private String getFieldName(int index, String axis) {
    return String.format("multiple2dscales.%d.%s", index, axis);
  }
  
  private String getColumnLabel(String pageTitle, String fieldCaption) {
    return String.format("%s/%s", pageTitle, fieldCaption);
  }
  
}
