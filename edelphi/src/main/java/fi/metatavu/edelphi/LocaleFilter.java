package fi.metatavu.edelphi;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.jsp.jstl.core.Config;

import org.apache.commons.lang.LocaleUtils;

import fi.metatavu.edelphi.utils.LocalizationUtils;

/**
 * Filter that is responsible of resolving used locale
 * 
 * @author Antti Leppä
 */
public class LocaleFilter implements Filter {
  
  @Override
  public void init(FilterConfig arg0) throws ServletException {
    // Nothing to initialize
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    ServletRequest servletRequest = request;
    try {
      if (request instanceof HttpServletRequest) {
        Locale locale = LocalizationUtils.resolveSupportedLocale(resolveLocale((HttpServletRequest) request));
        Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new fi.metatavu.edelphi.i18n.LocalizationContext(locale));
        servletRequest = new LocaleRequestWrapper((HttpServletRequest) request, locale);
      }
    } finally {
      filterChain.doFilter(servletRequest, response);
    }
  }

  @Override
  public void destroy() {
    // Nothing to destroy
  }

  public class LocaleRequestWrapper extends HttpServletRequestWrapper {

    private Locale locale;

    public LocaleRequestWrapper(HttpServletRequest req, Locale locale) {
      super(req);
      this.locale = locale;
    }

    @Override
    public Enumeration<Locale> getLocales() {
      return Collections.enumeration(Collections.singleton(getLocale()));
    }

    @Override
    public Locale getLocale() {
      return locale;
    }
  }
  
  private Locale resolveLocale(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("eDelphiLocale".equals(cookie.getName())) {
          return LocaleUtils.toLocale(cookie.getValue());
        }
      }
    }
    
    return request.getLocale();
  }
  
}
