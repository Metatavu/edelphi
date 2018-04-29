package fi.metatavu.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.birt.chart.model.Chart;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
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
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.comments.ReportPageCommentProcessor;
import fi.metatavu.edelphi.utils.comments.Scale1DReportPageCommentProcessor;

public class ThesisScale1DQueryReportPage extends QueryReportPageController {

  public ThesisScale1DQueryReportPage() {
    super(QueryPageType.THESIS_SCALE_1D);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    /**
     * Load fields on page
     */
    
    QueryOptionField queryOptionField = getOptionFieldFromScale1DPage(queryPage);
    List<QueryOptionFieldOption> queryFieldOptions = QueryUtils.listQueryOptionFieldOptions(queryOptionField);

    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, reportContext);
    Map<Long, Long> data = ReportUtils.getOptionListData(queryOptionField, queryFieldOptions, queryReplies);

    appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);

    QueryFieldDataStatistics statistics = ReportUtils.getOptionListStatistics(queryFieldOptions, data);
    
    statistics.setShift(-queryFieldOptions.size() / 2);
    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_scale_1d.jsp", statistics);
  }

  @Override
  public QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryReportPage reportPage = new QueryReportPage(queryPage.getId(), queryPage.getTitle(), "/jsp/blocks/panel/admin/report/scale1d.jsp");
    reportPage.setDescription(QueryPageUtils.getSetting(queryPage, "thesis.description"));
    reportPage.setThesis(QueryPageUtils.getSetting(queryPage, "thesis.text"));
    ReportUtils.appendComments(reportPage, queryPage, reportContext);

    // Add answers to comments and sort by them

    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

    QueryOptionField queryOptionField = getOptionFieldFromScale1DPage(queryPage);
    final Map<Long,String> answerMap = new HashMap<>();

    List<QueryReportPageComment> comments = reportPage.getComments();
    for (QueryReportPageComment comment : comments) {
      QueryReply queryReply = queryReplyDAO.findById(comment.getReplyId());
      QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryOptionField);
      answerMap.put(comment.getReplyId(), answer == null ? "-" : answer.getOption().getValue());
      if (answer != null) {
        String caption = StringUtils.capitalize(StringUtils.lowerCase(answer.getOption().getOptionField().getCaption()));
        comment.setAnswer(caption, answer.getOption().getText());
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
  
  /**
   * Appends query page comment to request.
   * 
   * @param requestContext request contract
   * @param queryPage query page
   */
  private void appendQueryPageComments(RequestContext requestContext, final QueryPage queryPage) {
    PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    Map<Long, Map<String, String>> answers = getRequestAnswerMap(requestContext);
    ReportPageCommentProcessor sorter = new Scale1DReportPageCommentProcessor(panelStamp, queryPage, answers);
    appendQueryPageComments(requestContext, queryPage, sorter);
  }
  
  private QueryOptionField getOptionFieldFromScale1DPage(QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO(); 
  
    List<QueryField> pageFields = queryFieldDAO.listByQueryPage(queryPage);
    
    if (pageFields.size() == 1) 
      return (QueryOptionField) pageFields.get(0);
    else
      throw new RuntimeException("");
  }
  
  @Override
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    QueryOptionField queryOptionField = getOptionFieldFromScale1DPage(queryPage);
    List<QueryOptionFieldOption> queryFieldOptions = QueryUtils.listQueryOptionFieldOptions(queryOptionField);

    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext.getReportContext());
    Map<Long, Long> data = ReportUtils.getOptionListData(queryOptionField, queryFieldOptions, queryReplies);
    
    List<Double> values = new ArrayList<>();
    List<String> categoryCaptions = new ArrayList<>();
    
    for (QueryOptionFieldOption optionFieldOption : queryFieldOptions) {
      Long optionId = optionFieldOption.getId();
      categoryCaptions.add(optionFieldOption.getText());
      values.add(new Double(data.get(optionId)));
    }
    
    QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
    QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
    QueryPageSettingKey queryPageSettingKey = queryPageSettingKeyDAO.findByName("scale1d.label");
    QueryPageSetting queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(queryPageSettingKey, queryPage);
    String xLabel = queryPageSetting == null ? null : queryPageSetting.getValue();

    QueryFieldDataStatistics statistics = ReportUtils.getOptionListStatistics(queryFieldOptions, data);
    
    Double avg = statistics.getCount() > 1 ? statistics.getAvg() : null;
    Double q1 = statistics.getCount() >= 5 ? statistics.getQ1() : null;
    Double q3 = statistics.getCount() >= 5 ? statistics.getQ3() : null;
    
    return ChartModelProvider.createBarChart(queryPage.getTitle(), xLabel, categoryCaptions, values, avg, q1, q3);
  }
}
