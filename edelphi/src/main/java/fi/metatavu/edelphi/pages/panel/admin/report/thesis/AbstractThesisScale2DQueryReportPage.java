package fi.metatavu.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.birt.chart.model.Chart;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
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
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageComment;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;

public abstract class AbstractThesisScale2DQueryReportPage extends QueryReportPageController {
  
  protected static final String RENDER_2D_AXIS_PARAM = "render2dAxis";
  protected static final String RENDER_2D_AXIS_X_OPTION = "x";
  protected static final String RENDER_2D_AXIS_Y_OPTION = "y";

  private static final int AVG_MIN_COUNT = 2;
  private static final int QUARTILE_MIN_COUNT = 5;

  public AbstractThesisScale2DQueryReportPage(QueryPageType queryPageType) {
    super(queryPageType);
  }
  
  protected Chart createBubbleChart(ChartContext chartContext, QueryPage queryPage, String title, String xLabel, String yLabel, String fieldNameX, String fieldNameY) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    
    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

    List<QueryOptionFieldOption> optionsX = QueryUtils.listQueryOptionFieldOptions(queryFieldX);
    List<QueryOptionFieldOption> optionsY = QueryUtils.listQueryOptionFieldOptions(queryFieldY);
    
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

        values[x][y] = values[x][y] != null ? values[x][y] + 1 : 1; 
      }
    }
    
    Map<Long, Long> dataX = ReportUtils.getOptionListData(queryFieldX, optionsX, queryReplies);
    Map<Long, Long> dataY = ReportUtils.getOptionListData(queryFieldY, optionsY, queryReplies);
    
    QueryFieldDataStatistics statisticsX = ReportUtils.getOptionListStatistics(optionsX, dataX);
    Double avgX = statisticsX.getCount() >= AVG_MIN_COUNT ? statisticsX.getAvg() : null;
    Double qX1 = statisticsX.getCount() >= QUARTILE_MIN_COUNT ? statisticsX.getQ1() : null;
    Double qX3 = statisticsX.getCount() >= QUARTILE_MIN_COUNT ? statisticsX.getQ3() : null;
    
    QueryFieldDataStatistics statisticsY = ReportUtils.getOptionListStatistics(optionsY, dataY);
    Double avgY = statisticsY.getCount() >= AVG_MIN_COUNT ? statisticsY.getAvg() : null;
    Double qY1 = statisticsY.getCount() >= QUARTILE_MIN_COUNT ? statisticsY.getQ1() : null;
    Double qY3 = statisticsY.getCount() >= QUARTILE_MIN_COUNT ? statisticsY.getQ3() : null;

    return ChartModelProvider.createBubbleChart(title, xLabel, xTickLabels, yLabel, yTickLabels, 0, 0, values, 
        avgX, qX1, qX3, avgY, qY1, qY3);
  }

  protected Chart createBarChart(ChartContext chartContext, QueryPage queryPage, String title, Render2dAxis render2dAxis, String fieldName) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    List<QueryOptionFieldOption> queryFieldOptions = QueryUtils.listQueryOptionFieldOptions(queryField);

    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext.getReportContext());
    Map<Long, Long> data = ReportUtils.getOptionListData(queryField, queryFieldOptions, queryReplies);
    
    List<Double> values = new ArrayList<>();
    List<String> categoryCaptions = new ArrayList<>();
    
    for (QueryOptionFieldOption optionFieldOption : queryFieldOptions) {
      Long optionId = optionFieldOption.getId();
      categoryCaptions.add(optionFieldOption.getText());
      values.add(Double.valueOf(data.get(optionId)));
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
    Double avg = statistics.getCount() >= AVG_MIN_COUNT ? statistics.getAvg() : null;
    Double q1 = statistics.getCount() >= QUARTILE_MIN_COUNT ? statistics.getQ1() : null;
    Double q3 = statistics.getCount() >= QUARTILE_MIN_COUNT ? statistics.getQ3() : null;
    
    // Bar chart rendering
    
    return ChartModelProvider.createBarChart(title, labelText, categoryCaptions, values, avg, q1, q3);
  }

  protected final class ReportCommentComparator implements Comparator<QueryReportPageComment> {
    
    private final Map<Long, String> answerMap;

    protected ReportCommentComparator(Map<Long, String> answerMap) {
      this.answerMap = answerMap;
    }

    @Override
    public int compare(QueryReportPageComment o1, QueryReportPageComment o2) {
      return answerMap.get(o2.getReplyId()).compareTo(answerMap.get(o1.getReplyId()));
    }
  }

  protected final class QuestionCommentComparator implements Comparator<QueryQuestionComment> {
    private final Map<Long, String> answerMap;

    protected QuestionCommentComparator(Map<Long, String> answerMap) {
      this.answerMap = answerMap;
    }

    @Override
    public int compare(QueryQuestionComment o1, QueryQuestionComment o2) {
      return answerMap.get(o2.getId()).compareTo(answerMap.get(o1.getId()));
    }
  }

  protected enum Render2dAxis {
    X,
    Y,
    BOTH
  }
}
