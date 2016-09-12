package fi.metatavu.edelphi;

import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;

public interface ActionedController {

  public DelfoiActionName getAccessActionName();
  
  public DelfoiActionScope getAccessActionScope();
  
}
