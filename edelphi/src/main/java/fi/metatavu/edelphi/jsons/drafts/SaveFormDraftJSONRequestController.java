package fi.metatavu.edelphi.jsons.drafts;

import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.dao.drafts.FormDraftDAO;
import fi.metatavu.edelphi.domainmodel.drafts.FormDraft;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class SaveFormDraftJSONRequestController extends JSONController {
  
  public void process(JSONRequestContext requestContext) {
    FormDraftDAO draftDAO = new FormDraftDAO(); 

    String url = RequestUtils.sortUrlQueryParams(requestContext.getRequest().getHeader("Referer"));
    String draftData = requestContext.getString("draftData");
    
    if (draftData != null) {
      User loggedUser = RequestUtils.getUser(requestContext);
      
      FormDraft formDraft = draftDAO.findByUrlAndUser(url, loggedUser);
      if (formDraft == null)
        formDraft = draftDAO.create(url, draftData, loggedUser);
      else
        draftDAO.update(formDraft, draftData);
      
      requestContext.addResponseParameter("url", formDraft.getUrl());
      requestContext.addResponseParameter("draftCreated", formDraft.getCreated());
      requestContext.addResponseParameter("draftModified", formDraft.getModified());
    } 
  }

}
