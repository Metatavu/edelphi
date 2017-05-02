package fi.metatavu.edelphi.utils;

import javax.servlet.http.HttpSession;

import fi.metatavu.edelphi.smvcj.controllers.RequestContext;

public class SessionUtils {

  private static final String HAS_UNREAD_IMPORTANT_BULLETINS = "hasUnreadImportantBulletins";
  private static final String THEME_ATTRIBUTE = "theme";
  
  private SessionUtils() {
  }
  
  public static String getCurrentTheme(HttpSession session) {
    return (String) session.getAttribute(THEME_ATTRIBUTE);
  }
  
  public static void setCurrentTheme(HttpSession session, String theme) {
    session.setAttribute(THEME_ATTRIBUTE, theme);
  }
 
  public static String getThemePath(RequestContext requestContext) {
    String currentTheme = getCurrentTheme(requestContext.getRequest().getSession());
    String baseUrl = RequestUtils.getBaseUrl(requestContext.getRequest());
    return baseUrl + "/_themes/" + currentTheme;
  }

  public static void setHasImportantBulletins(HttpSession session, Boolean hasImportantBulletins) {
    session.setAttribute(HAS_UNREAD_IMPORTANT_BULLETINS, hasImportantBulletins);
  }
  
  public static boolean hasImportantBulletins(HttpSession session) {
    Boolean result = (Boolean) session.getAttribute(HAS_UNREAD_IMPORTANT_BULLETINS);
    if (result == null) {
      return false;
    }

    return result;
  }
  
  
}
