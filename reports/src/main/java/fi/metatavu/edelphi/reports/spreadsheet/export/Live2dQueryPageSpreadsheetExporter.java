package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querymeta.QueryNumericFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.queries.ScatterValue;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;
import fi.metatavu.edelphi.reports.spreadsheet.comments.ReportPageCommentProcessor;

@ApplicationScoped
public class Live2dQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  private static final String FIELD_NAME_X = "x";
  private static final String FIELD_NAME_Y = "y";
  
  @Inject
  private QueryPageController queryPageController;

  @Inject
  private QueryReplyController queryReplyController;

  @Inject
  private QueryNumericFieldDAO queryNumericFieldDAO;

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();
    QueryNumericField queryFieldX = queryNumericFieldDAO.findByQueryPageAndName(queryPage, FIELD_NAME_X);
    QueryNumericField queryFieldY = queryNumericFieldDAO.findByQueryPageAndName(queryPage, FIELD_NAME_Y);
    
    int columnIndexX = exportContext.addColumn(queryPage.getTitle() + "/" + queryFieldX.getCaption());
    int columnIndexY = exportContext.addColumn(queryPage.getTitle() + "/" + queryFieldY.getCaption());
    
    List<ScatterValue> scatterValues = queryPageController.getLive2dScatterValues(queryPage, queryReplies);
    for (ScatterValue scatterValue : scatterValues) {
      QueryReply queryReply = queryReplyController.findQueryReply(scatterValue.getReplyId());
      exportContext.setCellValue(queryReply, columnIndexX, scatterValue.getX());
      exportContext.setCellValue(queryReply, columnIndexY, scatterValue.getY());
    }
  }

  @Override
  public ReportPageCommentProcessor exportComments(QueryPage queryPage, PanelStamp stamp, List<QueryReply> replies) {
    return null;
  }
  
}
