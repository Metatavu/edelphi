package fi.metatavu.edelphi.jsons.drafts;

import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.dao.drafts.FormDraftDAO;
import fi.metatavu.edelphi.domainmodel.drafts.FormDraft;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class RetrieveFormDraftJSONRequestController extends JSONController {
  
  public void process(JSONRequestContext requestContext) {
    FormDraftDAO draftDAO = new FormDraftDAO();

    String url = RequestUtils.sortUrlQueryParams(requestContext.getRequest().getHeader("Referer"));
    User loggedUser = RequestUtils.getUser(requestContext);
    
    FormDraft formDraft = draftDAO.findByUrlAndUser(url, loggedUser);
    if (formDraft == null) {
      requestContext.addResponseParameter("draftDeleted", true);
    } else {
      requestContext.addResponseParameter("draftDeleted", false);
      requestContext.addResponseParameter("url", formDraft.getUrl());
      requestContext.addResponseParameter("draftData", formDraft.getData());
      requestContext.addResponseParameter("draftCreated", formDraft.getCreated());
      requestContext.addResponseParameter("draftModified", formDraft.getModified());
    }
  }

}

