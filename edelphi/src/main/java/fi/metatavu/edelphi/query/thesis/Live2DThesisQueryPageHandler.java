package fi.metatavu.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryNumericFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.query.QueryExportContext;
import fi.metatavu.edelphi.query.QueryOption;
import fi.metatavu.edelphi.query.QueryOptionEditor;
import fi.metatavu.edelphi.query.QueryOptionType;
import fi.metatavu.edelphi.query.RequiredQueryFragment;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class Live2DThesisQueryPageHandler extends AbstractThesisQueryPageHandler {

  private static final String LABEL_X_OPTION = "live2d.label.x";
  private static final String COLOR_X_OPTION = "live2d.color.x";
  private static final String LABEL_Y_OPTION = "live2d.label.y";
  private static final String COLOR_Y_OPTION = "live2d.color.y";
  private static final String OPTIONS_X = "live2d.options.x";
  private static final String OPTIONS_Y = "live2d.options.y";
  private static final String FIELD_X = "x";
  private static final String FIELD_Y = "y";

  private List<QueryOption> options = new ArrayList<>();

  public Live2DThesisQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.QUESTION, LABEL_X_OPTION, "panelAdmin.block.query.live2d.options.labelX", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, COLOR_X_OPTION, "panelAdmin.block.query.live2d.options.colorX", QueryOptionEditor.COLOR, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, LABEL_Y_OPTION, "panelAdmin.block.query.live2d.options.labelY", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, COLOR_Y_OPTION, "panelAdmin.block.query.live2d.options.colorY", QueryOptionEditor.COLOR, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, OPTIONS_X, "panelAdmin.block.query.live2d.options.optionsX", QueryOptionEditor.OPTION_SET, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, OPTIONS_Y, "panelAdmin.block.query.live2d.options.optionsY", QueryOptionEditor.OPTION_SET, false));
  }

  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("live2d");

    Query query = queryPage.getQuerySection().getQuery();
    Panel panel = ResourceUtils.getResourcePanel(query);

    requiredFragment.addAttribute("panelId", panel.getId().toString());
    requiredFragment.addAttribute("queryId", query.getId().toString());
    requiredFragment.addAttribute("pageId", queryPage.getId().toString());
    requiredFragment.addAttribute("queryReplyId", queryReply.getId().toString());
    
    addRequiredFragment(requestContext, requiredFragment);
  }

  @Override
  protected void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    // Live 2d answers are saved with API
  }

  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    // Live reports are not supported by live 2d pages
  }

  @Override
  public void exportData(QueryExportContext exportContext) {
    // TODO: export data
  }

  @Override
  public List<QueryOption> getDefinedOptions() {
    List<QueryOption> result = new ArrayList<>(super.getDefinedOptions());
    result.addAll(this.options);
    return result;
  }
  
  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);

    for (QueryOption queryOption : getDefinedOptions()) {
      if ((queryOption.getType() == QueryOptionType.QUESTION) && ((!hasAnswers) || (queryOption.isEditableWithAnswers()))) {
        QueryPageUtils.setSetting(queryPage, queryOption.getName(), settings.get(queryOption.getName()), modifier);
      }
    }
    
    String labelX = settings.get(LABEL_X_OPTION);
    String labelY = settings.get(LABEL_Y_OPTION);
    Double minX = 0d;
    Double maxX = Double.valueOf(QueryPageUtils.getListSetting(queryPage, OPTIONS_X).size());
    Double minY = 0d;
    Double maxY = Double.valueOf(QueryPageUtils.getListSetting(queryPage, OPTIONS_Y).size());
    Double precision = 100d;
    
    synchronizeField(queryPage, FIELD_X, labelX, minX, maxX, precision, hasAnswers);
    synchronizeField(queryPage, FIELD_Y, labelY, minY, maxY, precision, hasAnswers);
  }
  
  private void synchronizeField(QueryPage queryPage, String fieldName, String label, Double min, Double max, Double precision, Boolean hasAnswers) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryNumericFieldDAO queryNumericFieldDAO = new QueryNumericFieldDAO();
    
    QueryNumericField queryNumericField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    
    if (queryNumericField != null) {
      queryFieldDAO.updateCaption(queryNumericField, label);
      if (!hasAnswers) {
        queryNumericFieldDAO.updateMin(queryNumericField, min);
        queryNumericFieldDAO.updateMax(queryNumericField, max);
        queryNumericFieldDAO.updatePrecision(queryNumericField, precision);
      }
    } else {
      queryNumericFieldDAO.create(queryPage, fieldName, Boolean.TRUE, label, min, max, precision);
    }
  }
  
}