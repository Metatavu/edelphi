package fi.metatavu.edelphi.pages.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.ReportUtils;

public class LiveQueryPageController extends PanelPageController {

  public LiveQueryPageController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelDAO panelDAO = new PanelDAO();
    QueryDAO queryDAO = new QueryDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    
    Long panelId = pageRequestContext.getLong("panelId");
    Long queryId = pageRequestContext.getLong("queryId");

    Panel panel = panelDAO.findById(panelId);
    Query query = queryDAO.findById(queryId);
    PanelStamp stamp = panel.getCurrentStamp();
    
    List<String> pageDatas = new ArrayList<>();

    for (QueryPage queryPage : queryPageDAO.listByQuery(query)) {
      try {
        pageDatas.add(new ObjectMapper().writeValueAsString(loadPageData(pageRequestContext, queryPage, stamp.getId())));
      } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    pageRequestContext.getRequest().setAttribute("pageDatas", pageDatas);
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/livequery.jsp");
  }

  private PageData loadPageData(RequestContext requestContext, QueryPage queryPage, Long stampId) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
    QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();

    ReportContext reportContext = new ReportContext(requestContext.getRequest().getLocale().toString(), stampId);

    // Render an ordinary 2D bubble chart

    String fieldNameX = getFieldName("x");
    String fieldNameY = getFieldName("y");
    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

    List<QueryOptionFieldOption> optionsX = queryOptionFieldOptionDAO.listByQueryField(queryFieldX);
    List<QueryOptionFieldOption> optionsY = queryOptionFieldOptionDAO.listByQueryField(queryFieldY);

    int maxX = 0;
    int maxY = 0;

    List<String> xTickLabels = new ArrayList<>();

    for (QueryOptionFieldOption optionX : optionsX) {
      int x = NumberUtils.createInteger(optionX.getValue());
      maxX = Math.max(maxX, x);
      xTickLabels.add(optionX.getText());
    }

    List<String> yTickLabels = new ArrayList<>();
    for (QueryOptionFieldOption optionY : optionsY) {
      int y = NumberUtils.createInteger(optionY.getValue());
      maxY = Math.max(maxY, y);
      yTickLabels.add(optionY.getText());
    }
//
//    maxX++;
//    maxY++;
//    
    Map<Long, Integer[]> values = new HashMap<>();

    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, reportContext);
    for (QueryReply queryReply : queryReplies) {
      Integer[] replyValues = new Integer[] {0, 0};
//      
//      Double[][] replyValues = new Double[maxX][];
//      for (int x = 0; x < maxX; x++) {
//        replyValues[x] = new Double[maxY];
//      }
//      
//      for (int x = 0; x < replyValues.length; x++) {
//        for (int y = 0; y < replyValues[x].length; y++) {
//          replyValues[x][y] = 0d;
//        }
//      }
//      
      QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX);
      QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);

      if (answerX != null && answerY != null) {
        int x = NumberUtils.createInteger(answerX.getOption().getValue());
        int y = NumberUtils.createInteger(answerY.getOption().getValue());
        replyValues[0] = x;
        replyValues[1] = y;
        
        
        // replyValues[x][y] += replyValues[x][y] + 1;
      }
      
      values.put(queryReply.getId(), replyValues);
    }

    QueryPageSettingKey queryPageSettingKey = queryPageSettingKeyDAO.findByName("scale2d.label.x");
    QueryPageSetting queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(queryPageSettingKey, queryPage);
    String xLabel = queryPageSetting == null ? null : queryPageSetting.getValue();
    queryPageSettingKey = queryPageSettingKeyDAO.findByName("scale2d.label.y");
    queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(queryPageSettingKey, queryPage);
    String yLabel = queryPageSetting == null ? null : queryPageSetting.getValue();

    return new PageData(queryPage.getId(), xLabel, yLabel, values, xTickLabels.toArray(new String[0]), yTickLabels.toArray(new String[0]));
  }

  private String getFieldName(String axis) {
    return "scale2d." + axis;
  }

  public class PageData {

    private Long queryPageId;
    private String xLabel;
    private String yLabel;
    private Map<Long, Integer[]> values;
    private String[] xTickLabels;
    private String[] yTickLabels;

    public PageData(Long queryPageId, String xLabel, String yLabel, Map<Long, Integer[]> values, String[] xTickLabels, String[] yTickLabels) {
      super();
      this.queryPageId = queryPageId;
      this.xLabel = xLabel;
      this.yLabel = yLabel;
      this.values = values;
      this.xTickLabels = xTickLabels;
      this.yTickLabels = yTickLabels;
    }
    
    public Long getQueryPageId() {
      return queryPageId;
    }

    public String getxLabel() {
      return xLabel;
    }

    public String getyLabel() {
      return yLabel;
    }
    
    public Map<Long, Integer[]> getValues() {
      return values;
    }

    public String[] getxTickLabels() {
      return xTickLabels;
    }

    public String[] getyTickLabels() {
      return yTickLabels;
    }
  }
}
