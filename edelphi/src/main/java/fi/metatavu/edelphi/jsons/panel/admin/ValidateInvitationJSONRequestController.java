package fi.metatavu.edelphi.jsons.panel.admin;

import java.util.Locale;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ValidateInvitationJSONRequestController extends JSONController {

  public ValidateInvitationJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_INVITATIONS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
   
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    String invitationMessage = jsonRequestContext.getString("invitationMessage");
    
    String acceptReplace = messages.getText(locale, "panel.admin.inviteUsers.acceptReplace");
    if (invitationMessage == null || invitationMessage.indexOf(acceptReplace) == -1) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_MAIL_TEMPLATE, messages.getText(locale, "exception.1000.noAcceptLink"));
    }
    
  }
  
}
