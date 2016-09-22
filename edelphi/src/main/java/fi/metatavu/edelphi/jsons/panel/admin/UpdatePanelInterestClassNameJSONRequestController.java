package fi.metatavu.edelphi.jsons.panel.admin;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.metatavu.edelphi.jsons.JSONController;

public class UpdatePanelInterestClassNameJSONRequestController extends JSONController {

  public UpdatePanelInterestClassNameJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long interestClassId = jsonRequestContext.getLong("interestClassId");
    String newName = jsonRequestContext.getString("name");
    PanelUserIntressClassDAO interestClassDAO = new PanelUserIntressClassDAO();
    PanelUserIntressClass expertiseClass = interestClassDAO.findById(interestClassId);
    interestClassDAO.updateName(expertiseClass, newName);
  }
  
}
