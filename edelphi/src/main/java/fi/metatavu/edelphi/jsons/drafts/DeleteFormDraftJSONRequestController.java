package fi.metatavu.edelphi.jsons.drafts;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.dao.drafts.FormDraftDAO;
import fi.metatavu.edelphi.domainmodel.drafts.FormDraft;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class DeleteFormDraftJSONRequestController extends JSONController {
  
  public void process(JSONRequestContext requestContext) {
    FormDraftDAO draftDAO = new FormDraftDAO();

    String url = RequestUtils.sortUrlQueryParams(requestContext.getRequest().getHeader("Referer"));
    String strategyStr = requestContext.getString("strategy");
    DeleteStrategy strategy = StringUtils.isEmpty(strategyStr) ? DeleteStrategy.URL_AND_USER : DeleteStrategy.valueOf(strategyStr);
    
    switch (strategy) {
      case URL:
        List<FormDraft> formDrafts = draftDAO.listByUrl(url);
        for (FormDraft formDraft : formDrafts) {
          draftDAO.delete(formDraft);
        }
        break;
      case URL_AND_USER:
        User loggedUser = RequestUtils.getUser(requestContext);
        FormDraft formDraft = draftDAO.findByUrlAndUser(url, loggedUser);
        if (formDraft != null) {
          draftDAO.delete(formDraft);
        } 
        break;
      default:
        throw new IllegalArgumentException("Unsupported strategy: " + strategyStr);
    }
  }

  private enum DeleteStrategy {
    URL,
    URL_AND_USER
  }

}
