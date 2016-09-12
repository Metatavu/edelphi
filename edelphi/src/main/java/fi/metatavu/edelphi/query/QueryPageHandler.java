package fi.metatavu.edelphi.query;

import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.smvc.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvc.controllers.RequestContext;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.users.User;

public interface QueryPageHandler {

  void renderPage(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply);
  void saveAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply);
  void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers);
  List<QueryOption> getDefinedOptions();
  void exportData(QueryExportContext exportContext);
  
}
