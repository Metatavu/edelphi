package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionGroupOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionGroupDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;

@ApplicationScoped
public class GroupingQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  @Inject
  private QueryFieldDAO queryFieldDAO;
  
  @Inject
  private QueryOptionFieldOptionGroupDAO queryOptionFieldOptionGroupDAO;
  
  @Inject
  private QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO;

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    QueryPage queryPage = exportContext.getQueryPage();
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName());
    List<QueryOptionFieldOptionGroup> fieldGroups = queryOptionFieldOptionGroupDAO.listByQueryField(queryField);
    
    for (QueryOptionFieldOptionGroup fieldGroup : fieldGroups) {
      int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + fieldGroup.getName());

      for (QueryReply queryReply : queryReplies) {
        List<QueryQuestionOptionGroupOptionAnswer> answers = queryQuestionOptionGroupOptionAnswerDAO.listByQueryReplyAndQueryFieldAndOptionFieldGroup(queryReply, queryField, fieldGroup);
        if (answers.size() > 0) {
          List<String> cellValues = new ArrayList<String>();
          
          for (QueryQuestionOptionGroupOptionAnswer answer : answers) {
            cellValues.add(answer.getOption().getText());
          }
          
          StringBuilder cellValueBuilder = new StringBuilder();
          for (int i = 0, l = cellValues.size(); i < l; i++) {
            cellValueBuilder.append(cellValues.get(i));
            if (i < (l - 1))
              cellValueBuilder.append(',');
          }
  
          exportContext.setCellValue(queryReply, columnIndex, cellValueBuilder.toString());
        }
      }
    }  

    exportComments(exportContext); 
  }

  private String getFieldName() {
    return "grouping";
  }
  
}
