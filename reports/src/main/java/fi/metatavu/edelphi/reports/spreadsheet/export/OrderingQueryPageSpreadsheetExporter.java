package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;

@ApplicationScoped
public class OrderingQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  @Inject
  private QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO;

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();

    int columnIndex = exportContext.addColumn(queryPage.getTitle());
    
    for (QueryReply queryReply : queryReplies) {
      List<QueryQuestionNumericAnswer> answers = queryQuestionNumericAnswerDAO.listByQueryReplyAndQueryPageOrderByData(queryReply, queryPage);
      StringBuilder cellValueBuilder = new StringBuilder();
      for (int i = 0, l = answers.size(); i < l; i++) {
        QueryNumericField queryNumericField =  (QueryNumericField) answers.get(i).getQueryField();
        
        cellValueBuilder.append(queryNumericField.getCaption());
        if (i < (l - 1))
          cellValueBuilder.append(',');
      }

      exportContext.setCellValue(queryReply, columnIndex, cellValueBuilder.toString());
    }

    exportComments(exportContext);
  }
  
}
