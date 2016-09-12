package fi.metatavu.edelphi.jsons.panel.admin;

import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.jsons.JSONController;

public class UpdatePanelExpertiseClassNameJSONRequestController extends JSONController {

  public UpdatePanelExpertiseClassNameJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long expertiseClassId = jsonRequestContext.getLong("expertiseClassId");
    String newName = jsonRequestContext.getString("name");
    PanelUserExpertiseClassDAO expertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserExpertiseClass expertiseClass = expertiseClassDAO.findById(expertiseClassId);
    expertiseClassDAO.updateName(expertiseClass, newName);
  }
  
}
