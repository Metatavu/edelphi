package fi.metatavu.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.birt.chart.model.Chart;

import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryFieldDataStatistics;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageComment;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ThesisScale2DQueryReportPage extends QueryReportPageController {

  public ThesisScale2DQueryReportPage() {
    super(QueryPageType.THESIS_SCALE_2D);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);
    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_scale_2d.jsp", null);
  }

  @Override
  public QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryReportPage reportPage = new QueryReportPage(queryPage.getId(), queryPage.getTitle(), "/jsp/blocks/panel/admin/report/scale2d.jsp");
    reportPage.setDescription(QueryPageUtils.getSetting(queryPage, "thesis.description"));
    reportPage.setThesis(QueryPageUtils.getSetting(queryPage, "thesis.text"));
    ReportUtils.appendComments(reportPage, queryPage, reportContext);
    
    // Add answers to comments and sort by them
    
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName("x"));
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName("y"));
    final Map<Long,String> answerMap = new HashMap<Long,String>();

    List<QueryReportPageComment> comments = reportPage.getComments();
    for (QueryReportPageComment comment : comments) {
      QueryReply queryReply = queryReplyDAO.findById(comment.getReplyId());
      QueryQuestionOptionAnswer xAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX); 
      QueryQuestionOptionAnswer yAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);
      answerMap.put(comment.getReplyId(), (xAnswer == null ? "-" : xAnswer.getOption().getValue()) + (yAnswer == null ? "-" : yAnswer.getOption().getValue()));
      if (xAnswer != null || yAnswer != null) {
        if (xAnswer != null) {
          String caption = StringUtils.capitalize(StringUtils.lowerCase(xAnswer.getOption().getOptionField().getCaption()));
          comment.setAnswer(caption, xAnswer.getOption().getText());
        }
        if (yAnswer != null) {
          String caption = StringUtils.capitalize(StringUtils.lowerCase(yAnswer.getOption().getOptionField().getCaption()));
          comment.setAnswer(caption, yAnswer.getOption().getText());
        }
      }
    }
    Collections.sort(comments, new Comparator<QueryReportPageComment>() {
      @Override
      public int compare(QueryReportPageComment o1, QueryReportPageComment o2) {
        return answerMap.get(o2.getReplyId()).compareTo(answerMap.get(o1.getReplyId()));
      }
    });
    
    return reportPage;
  }

  private void appendQueryPageComments(RequestContext requestContext, final QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName("x"));
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName("y"));
    PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    List<QueryQuestionComment> rootComments = queryQuestionCommentDAO.listRootCommentsByQueryPageAndStamp(queryPage, panelStamp);

    @SuppressWarnings("unchecked")
    Map<Long,Map<String,String>> answers = (Map<Long,Map<String,String>>) requestContext.getRequest().getAttribute("commentAnswers");
    if (answers == null) {
      answers = new HashMap<Long,Map<String,String>>();
      requestContext.getRequest().setAttribute("commentAnswers", answers);
    }
    final Map<Long,String> answerMap = new HashMap<Long,String>();
    for (QueryQuestionComment comment : rootComments) {
      QueryQuestionOptionAnswer xAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(comment.getQueryReply(), queryFieldX); 
      QueryQuestionOptionAnswer yAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(comment.getQueryReply(), queryFieldY);
      answerMap.put(comment.getId(), (xAnswer == null ? "-" : xAnswer.getOption().getValue()) + (yAnswer == null ? "-" : yAnswer.getOption().getValue()));
      if (xAnswer != null || yAnswer != null) {
        Map<String,String> valueMap = new LinkedHashMap<String,String>();
        answers.put(comment.getId(), valueMap);
        if (xAnswer != null) {
          String caption = StringUtils.capitalize(StringUtils.lowerCase(xAnswer.getOption().getOptionField().getCaption()));
          valueMap.put(caption, xAnswer.getOption().getText());
        }
        if (yAnswer != null) {
          String caption = StringUtils.capitalize(StringUtils.lowerCase(yAnswer.getOption().getOptionField().getCaption()));
          valueMap.put(caption, yAnswer.getOption().getText());
        }
      }
    }
    Collections.sort(rootComments, new Comparator<QueryQuestionComment>() {
      @Override
      public int compare(QueryQuestionComment o1, QueryQuestionComment o2) {
        return answerMap.get(o2.getId()).compareTo(answerMap.get(o1.getId()));
      }
    });

    Map<Long, List<QueryQuestionComment>> childComments = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    QueryUtils.appendQueryPageRootComments(requestContext, queryPage.getId(), rootComments);
    QueryUtils.appendQueryPageChildComments(requestContext, childComments);
  }
  
  @Override
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    
    // Data Access Objects

    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    
    // Determine whether 2D is rendered as bubble chart or as an X/Y axis bar chart   

    String axis = chartContext.getParameter(RENDER_2D_AXIS_PARAM);
    Render2dAxis render2dAxis = RENDER_2D_AXIS_X_OPTION.equals(axis) ? Render2dAxis.X : RENDER_2D_AXIS_Y_OPTION.equals(axis) ? Render2dAxis.Y : Render2dAxis.BOTH;
    
    if (render2dAxis == Render2dAxis.BOTH) {
    
      // Render an ordinary 2D bubble chart
      
      String fieldNameX = getFieldName("x");
      String fieldNameY = getFieldName("y");
      QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
      QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

      List<QueryOptionFieldOption> optionsX = queryOptionFieldOptionDAO.listByQueryField(queryFieldX);
      List<QueryOptionFieldOption> optionsY = queryOptionFieldOptionDAO.listByQueryField(queryFieldY);

      int maxX = 0;
      int maxY = 0;

      List<String> xTickLabels = new ArrayList<String>();

      for (QueryOptionFieldOption optionX : optionsX) {
        int x = NumberUtils.createInteger(optionX.getValue());
        maxX = Math.max(maxX, x);
        xTickLabels.add(optionX.getText());
      }

      List<String> yTickLabels = new ArrayList<String>();
      for (QueryOptionFieldOption optionY : optionsY) {
        int y = NumberUtils.createInteger(optionY.getValue());
        maxY = Math.max(maxY, y);
        yTickLabels.add(optionY.getText());
      }

      maxX++;
      maxY++;

      Double[][] values = new Double[maxX][];
      for (int x = 0; x < maxX; x++) {
        values[x] = new Double[maxY];
      }

      List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext.getReportContext());
      for (QueryReply queryReply : queryReplies) {
        QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX);
        QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);

        if (answerX != null && answerY != null) {
          int x = NumberUtils.createInteger(answerX.getOption().getValue());
          int y = NumberUtils.createInteger(answerY.getOption().getValue());

          values[x][y] = new Double(values[x][y] != null ? values[x][y] + 1 : 1); 
        }
      }

      QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
      QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
      QueryPageSettingKey queryPageSettingKey = queryPageSettingKeyDAO.findByName("scale2d.label.x");
      QueryPageSetting queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(queryPageSettingKey, queryPage);
      String xLabel = queryPageSetting == null ? null : queryPageSetting.getValue();
      queryPageSettingKey = queryPageSettingKeyDAO.findByName("scale2d.label.y");
      queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(queryPageSettingKey, queryPage);
      String yLabel = queryPageSetting == null ? null : queryPageSetting.getValue();

      return ChartModelProvider.createBubbleChart(queryPage.getTitle(), xLabel, xTickLabels, yLabel, yTickLabels, 0, 0, values);
    }
    else {

      // Render a bar chart of X or Y axis
      
      String fieldName = render2dAxis == Render2dAxis.X ? getFieldName("x") : getFieldName("y");
      QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      List<QueryOptionFieldOption> queryFieldOptions = queryOptionFieldOptionDAO.listByQueryField(queryField);

      List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext.getReportContext());
      Map<Long, Long> data = ReportUtils.getOptionListData(queryField, queryFieldOptions, queryReplies);
      
      List<Double> values = new ArrayList<Double>();
      List<String> categoryCaptions = new ArrayList<String>();
      
      for (QueryOptionFieldOption optionFieldOption : queryFieldOptions) {
        Long optionId = optionFieldOption.getId();
        categoryCaptions.add(optionFieldOption.getText());
        values.add(new Double(data.get(optionId)));
      }
      
      // Axis label
      
      QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
      QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
      String labelSettingName = render2dAxis == Render2dAxis.X ? "scale2d.label.x" : "scale2d.label.y";
      QueryPageSettingKey queryPageSettingKey = queryPageSettingKeyDAO.findByName(labelSettingName);
      QueryPageSetting queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(queryPageSettingKey, queryPage);
      String labelText = queryPageSetting == null ? null : queryPageSetting.getValue();

      // Statistics
      // TODO These could be calculated elsewhere and added below the chart image?
      
      QueryFieldDataStatistics statistics = ReportUtils.getOptionListStatistics(queryFieldOptions, data);
      Double avg = statistics.getCount() > 1 ? statistics.getAvg() : null;
      Double q1 = statistics.getCount() >= 5 ? statistics.getQ1() : null;
      Double q3 = statistics.getCount() >= 5 ? statistics.getQ3() : null;
      
      // Bar chart rendering
      
      return ChartModelProvider.createBarChart(queryPage.getTitle(), labelText, categoryCaptions, values, avg, q1, q3);
    }
  }
  

  private String getFieldName(String axis) {
    return "scale2d." + axis;
  }
  
  private final static String RENDER_2D_AXIS_PARAM = "render2dAxis";
  private final static String RENDER_2D_AXIS_X_OPTION = "x";
  private final static String RENDER_2D_AXIS_Y_OPTION = "y";

  private enum Render2dAxis {
    X,
    Y,
    BOTH;
  }
 
}
