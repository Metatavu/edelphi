package fi.metatavu.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.chart.model.Chart;

import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionGroupOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionGroupDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageComment;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageGrouping;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ThesisGroupingQueryReportPage extends QueryReportPageController {

  public ThesisGroupingQueryReportPage() {
    super(QueryPageType.THESIS_GROUPING);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    /**
     * Load fields on page
     */

    QueryOptionFieldOptionGroupDAO groupDAO = new QueryOptionFieldOptionGroupDAO();
    
    QueryOptionField queryOptionField = getOptionFieldFromGroupingPage(queryPage);

    List<QueryOptionFieldOptionGroup> groups = groupDAO.listByQueryField(queryOptionField);
    
    
    appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);

    return new ThesisGroupingQueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_grouping.jsp", groups);
  }

  @Override
  public QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryReportPageGrouping reportPage = new QueryReportPageGrouping(queryPage.getId(), queryPage.getTitle(), "/jsp/blocks/panel/admin/report/grouping.jsp");

    reportPage.setDescription(QueryPageUtils.getSetting(queryPage, "thesis.description"));
    reportPage.setThesis(QueryPageUtils.getSetting(queryPage, "thesis.text"));
    ReportUtils.appendComments(reportPage, queryPage, reportContext);

    // Add answers to comments and sort by them

    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO = new QueryQuestionOptionGroupOptionAnswerDAO();
    QueryOptionFieldOptionGroupDAO queryOptionFieldOptionGroupDAO = new QueryOptionFieldOptionGroupDAO();
    
    QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, "grouping");
    QueryOptionField queryOptionField = getOptionFieldFromGroupingPage(queryPage);
    List<QueryOptionFieldOptionGroup> groups = queryOptionFieldOptionGroupDAO.listByQueryField(queryOptionField);
    for (QueryOptionFieldOptionGroup group : groups) {
      reportPage.addGroupId(group.getId());
    }

    List<QueryReportPageComment> comments = reportPage.getComments();
    for (QueryReportPageComment comment : comments) {
      QueryReply queryReply = queryReplyDAO.findById(comment.getReplyId());
      List<QueryQuestionOptionGroupOptionAnswer> groupingAnswers = queryQuestionOptionGroupOptionAnswerDAO.listByQueryReplyAndQueryField(queryReply, queryField);
      if (!groupingAnswers.isEmpty()) {
        for (QueryQuestionOptionGroupOptionAnswer groupingAnswer : groupingAnswers) {
          // TODO group order?
          String groupName = groupingAnswer.getGroup().getName();
          String optionName = groupingAnswer.getOption().getText();
          comment.setAnswer(groupName, comment.getAnswer(groupName) != null ? comment.getAnswer(groupName) + ", " + optionName : optionName);
        }
      }
    }
    
    return reportPage;
  }

  private QueryOptionField getOptionFieldFromGroupingPage(QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO(); 
  
    List<QueryField> pageFields = queryFieldDAO.listByQueryPage(queryPage);
    
    if (pageFields.size() == 1) 
      return (QueryOptionField) pageFields.get(0);
    else
      throw new RuntimeException("");
  }
  
  @Override
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryOptionFieldOptionGroupDAO groupDAO = new QueryOptionFieldOptionGroupDAO();

    Long groupId = chartContext.getLong("groupId");
    QueryOptionField queryOptionField = getOptionFieldFromGroupingPage(queryPage);
    QueryOptionFieldOptionGroup group = groupDAO.findById(groupId);
    List<QueryOptionFieldOption> queryFieldOptions = queryOptionFieldOptionDAO.listByQueryField(queryOptionField);

    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext.getReportContext());
    Map<Long, Long> listOptionAnswerCounts = ReportUtils.getGroupData(queryOptionField, group, queryFieldOptions, queryReplies);
    
    List<String> categoryCaptions = new ArrayList<String>();
    List<Double> values = new ArrayList<Double>();
    
    for (QueryOptionFieldOption optionFieldOption : queryFieldOptions) {
      Long optionId = optionFieldOption.getId();
      categoryCaptions.add(optionFieldOption.getText());
      values.add(new Double(listOptionAnswerCounts.get(optionId)));
    }
    
    return ChartModelProvider.createPieChart(group.getName(), categoryCaptions, values);
  }

  private void appendQueryPageComments(RequestContext requestContext, QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    QueryQuestionOptionGroupOptionAnswerDAO groupDAO = new QueryQuestionOptionGroupOptionAnswerDAO();

    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    List<QueryQuestionComment> rootComments = queryQuestionCommentDAO.listRootCommentsByQueryPageAndStamp(queryPage, panelStamp);
    @SuppressWarnings("unchecked")
    Map<Long,Map<String,String>> answers = (Map<Long,Map<String,String>>) requestContext.getRequest().getAttribute("commentAnswers");
    
    if (answers == null) {
      answers = new HashMap<Long,Map<String,String>>();
      requestContext.getRequest().setAttribute("commentAnswers", answers);
    }
    QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, "grouping");
    for (QueryQuestionComment rootComment : rootComments) {
      QueryReply reply = queryReplyDAO.findByUserAndQueryAndStamp(rootComment.getCreator(), queryPage.getQuerySection().getQuery(), panelStamp);
      List<QueryQuestionOptionGroupOptionAnswer> groupingAnswers = groupDAO.listByQueryReplyAndQueryField(reply, queryField);
      if (!groupingAnswers.isEmpty()) {
        Map<String,String> valueMap = new LinkedHashMap<String,String>();
        answers.put(rootComment.getId(), valueMap);
        for (QueryQuestionOptionGroupOptionAnswer groupingAnswer : groupingAnswers) {
          // TODO group order?
          String groupName = groupingAnswer.getGroup().getName();
          String optionName = groupingAnswer.getOption().getText();
          valueMap.put(groupName, valueMap.get(groupName) != null ? valueMap.get(groupName) + ", " + optionName : optionName);
        }
      }
    }
    
    Map<Long, List<QueryQuestionComment>> childComments = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    QueryUtils.appendQueryPageRootComments(requestContext, queryPage.getId(), rootComments);
    QueryUtils.appendQueryPageChildComments(requestContext, childComments);
  }
  
  public class ThesisGroupingQueryReportPageData extends QueryReportPageData {
    private List<QueryOptionFieldOptionGroup> groups;

    public ThesisGroupingQueryReportPageData(QueryPage queryPage, String jspFile, List<QueryOptionFieldOptionGroup> groups) {
      super(queryPage, jspFile, null);
      
      this.groups = groups;
    }
    
    public List<QueryOptionFieldOptionGroup> getGroups() {
      return this.groups;
    }
  }
}
