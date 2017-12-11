package fi.metatavu.edelphi;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.utils.SessionUtils;
import fi.metatavu.edelphi.utils.SystemUtils;

/**
 * Filter for setting common settings for all requests
 * 
 * @author Antti Leppä
 */
public class CommonsFilter implements Filter {
  
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public void init(FilterConfig arg0) throws ServletException {
    // Nothing to init
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    GenericDAO.setEntityManager(entityManager);
    try {
      if (request instanceof HttpServletRequest) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        session.setAttribute("delfoiId", 1l);
        SessionUtils.setCurrentTheme(session, "default");
        request.setAttribute("googleAnalyticsTrackingId", SystemUtils.getSettingValue("googleAnalytics.trackingId"));
      }
  
      filterChain.doFilter(request, response);
    } finally {
      GenericDAO.setEntityManager(null);
    }
  }

  @Override
  public void destroy() {
    // Nothing to destroy
  }
}
