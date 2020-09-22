package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionTextAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionTextAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;

@ApplicationScoped
public class FormQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  @Inject
  private QueryFieldDAO queryFieldDAO;
  
  @Inject
  private QueryQuestionTextAnswerDAO queryQuestionTextAnswerDAO;
  
  @Inject
  private QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO;
  
  @Inject
  private QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO;

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    List<QueryReply> queryReplies = exportContext.getQueryReplies();

    QueryPage queryPage = exportContext.getQueryPage();

    List<QueryField> queryFields = queryFieldDAO.listByQueryPage(queryPage);
    for (QueryField queryField : queryFields) {
      int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + queryField.getCaption());
      
      for (QueryReply queryReply : queryReplies) {
        switch (queryField.getType()) {
        case OPTIONFIELD:
          QueryQuestionOptionAnswer optionAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
          exportContext.setCellValue(queryReply, columnIndex, optionAnswer != null ? optionAnswer.getOption().getText() : null);
          break;
        case NUMERIC:
        case NUMERIC_SCALE:
          QueryQuestionNumericAnswer numericAnswer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply,  queryField);
          exportContext.setCellValue(queryReply, columnIndex, numericAnswer != null ? numericAnswer.getData() : null);
          break;
        case TEXT:
          QueryQuestionTextAnswer textAnswer = queryQuestionTextAnswerDAO.findByQueryReplyAndQueryField(queryReply,  queryField);
          exportContext.setCellValue(queryReply, columnIndex, textAnswer != null ? textAnswer.getData() : null);
          break;
        }
      }
    }

    exportComments(exportContext);
  }
  
}
