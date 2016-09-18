package fi.metatavu.edelphi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import fi.metatavu.edelphi.utils.SessionUtils;

/**
 * 
 * @author Antti Leppä
 */
public class DefaultsFilter implements Filter {

  @Override
  public void init(FilterConfig arg0) throws ServletException {
    // Nothing to init
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      HttpSession session = ((HttpServletRequest) request).getSession();
      session.setAttribute("delfoiId", 1l);
      SessionUtils.setCurrentTheme(session, "default");
    }

    filterChain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // Nothing to destroy
  }
}
