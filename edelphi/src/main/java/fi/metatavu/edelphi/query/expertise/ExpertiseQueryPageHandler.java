package fi.metatavu.edelphi.query.expertise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.query.AbstractQueryPageHandler;
import fi.metatavu.edelphi.query.QueryExportContext;
import fi.metatavu.edelphi.query.QueryOption;
import fi.metatavu.edelphi.query.QueryOptionEditor;
import fi.metatavu.edelphi.query.QueryOptionType;
import fi.metatavu.edelphi.query.RequiredQueryFragment;
import fi.metatavu.edelphi.utils.QueryDataUtils;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class ExpertiseQueryPageHandler extends AbstractQueryPageHandler {

  public ExpertiseQueryPageHandler() {
    super();
    options.add(new QueryOption(QueryOptionType.QUESTION, "expertise.description", "panelAdmin.block.query.expertiseDescriptionOptionLabel", QueryOptionEditor.MEMO, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "expertise.showLiveReport", "panelAdmin.block.query.expertiseShowLiveReportOptionLabel", QueryOptionEditor.BOOLEAN, true));
  }

  @Override
  public void renderPage(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    
    // Data access objects
    
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();
    
    // Expertise description

    RequiredQueryFragment descriptionFragment = new RequiredQueryFragment("description");
    descriptionFragment.addAttribute("text", getStringOptionValue(queryPage, getDefinedOption("expertise.description")));
    addRequiredFragment(requestContext, descriptionFragment);
    
    // Expertise matrix

    Query query = queryPage.getQuerySection().getQuery();
    Panel panel = ResourceUtils.getResourcePanel(query);
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
      
      RequiredQueryFragment requiredFragment = new RequiredQueryFragment("expertise");

      int i = 0;
      requiredFragment.addAttribute("expertiseClasses.count", String.valueOf(expertiseClasses.size()));
      for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
        QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName(expertiseClass));
        QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);

        if (queryField != null) {
          requiredFragment.addAttribute("expertiseClass." + i + ".id", expertiseClass.getId().toString());
          requiredFragment.addAttribute("expertiseClass." + i + ".name", expertiseClass.getName());
          i++;

          if (answer != null) {
            for (QueryOptionFieldOption option : answer.getOptions()) {
              requiredFragment.addAttribute("expertiseClass." + expertiseClass.getId().toString() + "." + option.getValue() + ".selected", "1");
            }
          }
        } else {
          throw new IllegalStateException("Could not find query field");
        }
      }
      
      i = 0;
      requiredFragment.addAttribute("intrestClasses.count", String.valueOf(intrestClasses.size()));
      for (PanelUserIntressClass intrestClass : intrestClasses) {
        requiredFragment.addAttribute("intrestClass." + i + ".id", intrestClass.getId().toString());
        requiredFragment.addAttribute("intrestClass." + i + ".name", intrestClass.getName());
        i++;
      }

      addRequiredFragment(requestContext, requiredFragment);
      
      if (getBooleanOptionValue(queryPage, getDefinedOption("expertise.showLiveReport")))
        renderReport(requestContext, queryPage, expertiseClasses, intrestClasses);
    } else {
      throw new IllegalStateException("Could not find query panel");
    }
  }

  @Override
  public void saveAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();

    Query query = queryPage.getQuerySection().getQuery();
    Panel panel = ResourceUtils.getResourcePanel(query);
    if (panel != null) {
      List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(panel);
      List<PanelUserIntressClass> intrestClasses = panelUserIntressClassDAO.listByPanel(panel);
      
      for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
        String fieldName = getFieldName(expertiseClass);

        QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
        if (queryField != null) {
          Set<QueryOptionFieldOption> selectedOptions = new HashSet<QueryOptionFieldOption>();

          for (PanelUserIntressClass intrestClass : intrestClasses) {
            QueryOptionFieldOption option = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryField, intrestClass.getId().toString());
            if (option != null) {
              Boolean selected = "1".equals(requestContext.getString("expertise." + expertiseClass.getId() + "." + intrestClass.getId()));
              if (selected) {
                selectedOptions.add(option);
              }
            } else {
              throw new IllegalArgumentException("Could not save reply, query option not found");
            }
          }

          QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
          if (answer != null) {
            if (query.getAllowEditReply()) {
              if (selectedOptions.size() > 0) {
                answer = queryQuestionMultiOptionAnswerDAO.updateOptions(answer, selectedOptions);
              } else {
                queryQuestionMultiOptionAnswerDAO.delete(answer);
              }
            } else {
              throw new IllegalStateException("Could not save reply: Already replied");
            }
          } else {
            if (selectedOptions.size() > 0)
              answer = queryQuestionMultiOptionAnswerDAO.create(queryReply, queryField, selectedOptions);
          }
        } else {
          throw new IllegalArgumentException("Could not save reply, query field not found");
        }
      }

    } else {
      throw new IllegalStateException("Could not find query panel");
    }
  }

  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);

    QueryPageUtils.setSetting(queryPage, "expertise.description", settings.get("expertise.description"), modifier);
    QueryPageUtils.setSetting(queryPage, "expertise.showLiveReport", settings.get("expertise.showLiveReport"), modifier);

    if (!hasAnswers) {
      synchronizedFields(queryPage);
    }
  }

  public void synchronizedFields(QueryPage queryPage) {
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldDAO queryOptionFieldDAO = new QueryOptionFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionAnswerDAO queryQuestionAnswerDAO = new QueryQuestionAnswerDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
 
    Query query = queryPage.getQuerySection().getQuery();
    Panel panel = ResourceUtils.getResourcePanel(query);
    if (panel != null) {
      Boolean mandatory = false; // TODO: Mandatory ???
 
      List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(panel);
      List<PanelUserIntressClass> intrestClasses = panelUserIntressClassDAO.listByPanel(panel);
 
      Set<String> deprecatedQueryFields = new HashSet<String>();
      List<QueryField> oldQueryFields = queryFieldDAO.listByQueryPage(queryPage);
      for (QueryField oldQueryField : oldQueryFields) {
        deprecatedQueryFields.add(oldQueryField.getName());
      }
 
      for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
        String fieldName = getFieldName(expertiseClass);
        String caption = expertiseClass.getName();
 
        QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
        if (queryField == null) {
          queryField = queryOptionFieldDAO.create(queryPage, fieldName, mandatory, caption);
        } else {
          queryFieldDAO.updateCaption(queryField, caption);
          queryFieldDAO.updateMandatory(queryField, mandatory);
        }
 
        deprecatedQueryFields.remove(queryField.getName());
 
        Set<String> deprecatedOptions = new HashSet<String>();
        List<QueryOptionFieldOption> oldOptions = queryOptionFieldOptionDAO.listByQueryField(queryField);
        for (QueryOptionFieldOption oldOption : oldOptions) {
          deprecatedOptions.add(oldOption.getValue());
        }
 
        for (PanelUserIntressClass intrestClass : intrestClasses) {
          QueryOptionFieldOption option = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryField, intrestClass.getId().toString());
          if (option == null) {
            option = queryOptionFieldOptionDAO.create(queryField, intrestClass.getName(), intrestClass.getId().toString());
          } else {
            queryOptionFieldOptionDAO.updateText(option, intrestClass.getName());
          }
 
          deprecatedOptions.remove(option.getValue());
        }
 
        for (String deprecatedOption : deprecatedOptions) {
          QueryOptionFieldOption option = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryField, deprecatedOption);
          if (option != null) {
            long answerCount = queryQuestionOptionAnswerDAO.countByQueryOptionFieldOption(option);
            if (answerCount == 0) {
              queryOptionFieldOptionDAO.delete(option);
            }
            else {
              queryOptionFieldOptionDAO.archive(option);
            }
          }
        }
      }

      for (String deprecatedQueryField : deprecatedQueryFields) {
        QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, deprecatedQueryField);
        if (queryField != null) {
          List<QueryOptionFieldOption> options = queryOptionFieldOptionDAO.listByQueryField((QueryOptionField) queryField);
          for (QueryOptionFieldOption option : options) {
            long answerCount = queryQuestionOptionAnswerDAO.countByQueryOptionFieldOption(option);
            if (answerCount== 0) {
              queryOptionFieldOptionDAO.delete(option);
            }
            else {
              queryOptionFieldOptionDAO.archive(option);
            }
          }
          long answerCount = queryQuestionAnswerDAO.countByQueryField(queryField);
          if (answerCount == 0) {
            queryFieldDAO.delete(queryField);
          }
          else {
            queryFieldDAO.archive(queryField);
          }
        }
      }
 
    } else {
      throw new IllegalStateException("Could not find query panel");
    }
  }

  @Override
  public void exportData(QueryExportContext exportContext) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();

    QueryPage queryPage = exportContext.getQueryPage();
    Query query = queryPage.getQuerySection().getQuery();
    Panel panel = ResourceUtils.getResourcePanel(query);
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

            exportContext.addCellValue(queryReply, columnIndex, cellValueBuilder.toString());
          }
        }        
      }
    }
  }
  
  @Override
  public List<QueryOption> getDefinedOptions() {
    List<QueryOption> options = new ArrayList<QueryOption>(super.getDefinedOptions());
    options.addAll(this.options);
    return options;
  }

  private void renderReport(PageRequestContext requestContext, QueryPage queryPage, List<PanelUserExpertiseClass> expertiseClasses, List<PanelUserIntressClass> interestClasses) {
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("report_bubblechart");
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();
    UserDAO userDAO = new UserDAO();
    
    List<String> xTickLabels = new ArrayList<String>();
    List<String> yTickLabels = new ArrayList<String>();
    
    Map<Long, Integer> expertiseIndexMap = new HashMap<Long, Integer>();
    Map<Long, Integer> interestIndexMap = new HashMap<Long, Integer>();
    
    int expertiseClassCount = expertiseClasses.size();
    int interestClassCount = interestClasses.size();
    
    for (int i = 0; i < expertiseClassCount; i++) {
      PanelUserExpertiseClass expertiseClass = expertiseClasses.get(i);
      expertiseIndexMap.put(expertiseClass.getId(), i);
      xTickLabels.add(expertiseClass.getName());
    }
    
    for (int i = 0; i < interestClassCount; i++) {
      PanelUserIntressClass interestClass = interestClasses.get(i);
      interestIndexMap.put(interestClass.getId(), (interestClassCount - 1) - i);
      yTickLabels.add(0, interestClass.getName());
    }

    int maxX = expertiseClassCount + 1;
    int maxY = interestClassCount + 1;
        
    Double[][] values = new Double[maxX][];
    for (int x = 0; x < maxX; x++) {
      values[x] = new Double[maxY];
    }
    
    List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStamp(queryPage.getQuerySection().getQuery(), RequestUtils.getActiveStamp(requestContext));
    User loggedUser = requestContext.isLoggedIn() ? userDAO.findById(requestContext.getLoggedUserId()) : null;
    QueryReply excludeReply = QueryDataUtils.findQueryReply(requestContext, loggedUser, queryPage.getQuerySection().getQuery());
    
    for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
      QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName(expertiseClass));
      for (QueryReply queryReply : queryReplies) {
        if ((excludeReply == null)||(!queryReply.getId().equals(excludeReply.getId()))) {
          QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
          
          if (answer != null) {
            for (QueryOptionFieldOption option : answer.getOptions()) {
              Long interestId = NumberUtils.createLong(option.getValue());
  
              int indexX = expertiseIndexMap.get(expertiseClass.getId());
              int indexY = interestIndexMap.get(interestId);
  
              values[indexX][indexY] = new Double(values[indexX][indexY] != null ? values[indexX][indexY] + 1 : 1); 
            }
          }
        }
      }
    }

    requiredFragment.addAttribute("xAxisLabel", Messages.getInstance().getText(requestContext.getRequest().getLocale(), "panelAdmin.block.query.expertiseLiveReportXAxisTitle"));
    requiredFragment.addAttribute("yAxisLabel", Messages.getInstance().getText(requestContext.getRequest().getLocale(), "panelAdmin.block.query.expertiseLiveReportYAxisTitle"));
    requiredFragment.addAttribute("xValueCount", String.valueOf(maxX));
    requiredFragment.addAttribute("yValueCount", String.valueOf(maxY));
    
    for (int i = 0, l = xTickLabels.size(); i < l; i++) {
      requiredFragment.addAttribute("xTickLabel." + i, xTickLabels.get(i)); 
    }
    
    for (int i = 0, l = yTickLabels.size(); i < l; i++) {
      requiredFragment.addAttribute("yTickLabel." + i, yTickLabels.get(i)); 
    }
    
    for (int x = 0; x < maxX; x++) {
      for (int y = 0; y < maxY; y++) {
        if (values[x][y] != null) {
          requiredFragment.addAttribute("bubble." + x + "." + y + ".x", String.valueOf((x)));
          requiredFragment.addAttribute("bubble." + x + "." + y + ".y", String.valueOf((y)));
          requiredFragment.addAttribute("bubble." + x + "." + y + ".value", String.valueOf(values[x][y]));
        }
      }
    }     
    
    addRequiredFragment(requestContext, requiredFragment);
  }
  
  private String getFieldName(PanelUserExpertiseClass expertiseClass) {
    return "expertise." + expertiseClass.getId();
  }
  
  private List<QueryOption> options = new ArrayList<QueryOption>();
}
