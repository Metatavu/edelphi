package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;
import fi.metatavu.edelphi.resources.ResourceController;

@ApplicationScoped
public class ExpertiseQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  @Inject
  private ResourceController resourceController;

  @Inject
  private QueryFieldDAO queryFieldDAO;

  @Inject
  private PanelUserExpertiseClassDAO panelUserExpertiseClassDAO;
  
  @Inject
  private PanelUserIntressClassDAO panelUserIntressClassDAO;
  
  @Inject
  private QueryOptionFieldOptionDAO queryOptionFieldOptionDAO;
  
  @Inject
  private QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO;

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    QueryPage queryPage = exportContext.getQueryPage();
    Query query = queryPage.getQuerySection().getQuery();
    Panel panel = resourceController.getResourcePanel(query);
    if (panel != null) {
      List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(panel);
      Collections.sort(expertiseClasses, new Comparator<PanelUserExpertiseClass>() {
        @Override
        public int compare(PanelUserExpertiseClass o1, PanelUserExpertiseClass o2) {
          return o1.getId().compareTo(o2.getId());
        }
      });

      List<PanelUserIntressClass> intrestClasses = panelUserIntressClassDAO.listByPanel(panel);
      Collections.sort(intrestClasses, new Comparator<PanelUserIntressClass>() {
        @Override
        public int compare(PanelUserIntressClass o1, PanelUserIntressClass o2) {
          return o1.getId().compareTo(o2.getId());
        }
      });

      List<QueryReply> queryReplies = exportContext.getQueryReplies();

      for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
        String fieldName = getFieldName(expertiseClass);
        QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
        int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + queryField.getCaption()); 
        
        for (QueryReply queryReply : queryReplies) {
          List<String> interests = new ArrayList<String>();
          
          QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
          if (answer != null) {
            for (PanelUserIntressClass intrestClass : intrestClasses) {
              QueryOptionFieldOption option = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryField, intrestClass.getId().toString());
              if (answer.getOptions().contains(option)) {
                interests.add(option.getText());
              }
            } 
            
            StringBuilder cellValueBuilder = new StringBuilder();
            for (int i = 0, l = interests.size(); i < l; i++) {
              cellValueBuilder.append(interests.get(i));
              if (i < (l - 1))
                cellValueBuilder.append(',');
            }

            exportContext.setCellValue(queryReply, columnIndex, cellValueBuilder.toString());
          }
        }        
      }
    }
  }
  
  private String getFieldName(PanelUserExpertiseClass expertiseClass) {
    return "expertise." + expertiseClass.getId();
  }

}
