package fi.metatavu.edelphi.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.query.collage.Collage2DQueryPageHandler;
import fi.metatavu.edelphi.query.expertise.ExpertiseQueryPageHandler;
import fi.metatavu.edelphi.query.form.FormQueryPageHandler;
import fi.metatavu.edelphi.query.text.TextQueryPageHandler;
import fi.metatavu.edelphi.query.thesis.GroupingThesisQueryPageHandler;
import fi.metatavu.edelphi.query.thesis.MultiSelectThesisQueryPageHandler;
import fi.metatavu.edelphi.query.thesis.OrderingThesisQueryPageHandler;
import fi.metatavu.edelphi.query.thesis.Scale1DThesisQueryPageHandler;
import fi.metatavu.edelphi.query.thesis.Scale2DThesisQueryPageHandler;
import fi.metatavu.edelphi.query.thesis.TimeSerieThesisQueryPageHandler;
import fi.metatavu.edelphi.query.thesis.TimelineThesisQueryPageHandler;

public class QueryPageHandlerFactory {
  
  private static final QueryPageHandlerFactory INSTANCE = new QueryPageHandlerFactory();
  
  public static QueryPageHandlerFactory getInstance() {
    return INSTANCE;
  }
  
  public QueryPageHandlerFactory() {
    registerPageHandler(QueryPageType.TEXT, TextQueryPageHandler.class);
    registerPageHandler(QueryPageType.FORM, FormQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_TIME_SERIE, TimeSerieThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_SCALE_1D, Scale1DThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_SCALE_2D, Scale2DThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.EXPERTISE, ExpertiseQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_MULTI_SELECT, MultiSelectThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_ORDER, OrderingThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_GROUPING, GroupingThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_TIMELINE, TimelineThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.COLLAGE_2D, Collage2DQueryPageHandler.class);
  }

  public QueryPageHandler buildPageHandler(QueryPageType queryPageType) {
    try {
      return pageHandlers.get(queryPageType).newInstance();
    } catch (Exception e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
    }
  }
  
  public Set<QueryPageType> getRegisteredTypes() {
    return pageHandlers.keySet();
  }
  
  private void registerPageHandler(QueryPageType queryPageType, Class<? extends QueryPageHandler> pageHandler) {
    pageHandlers.put(queryPageType, pageHandler);
  }
  
  private Map<QueryPageType, Class<? extends QueryPageHandler>> pageHandlers = new HashMap<QueryPageType, Class<? extends QueryPageHandler>>();
}
