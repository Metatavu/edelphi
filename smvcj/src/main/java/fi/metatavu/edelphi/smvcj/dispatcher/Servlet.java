package fi.metatavu.edelphi.smvcj.dispatcher;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.StatusCode;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestController;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestController;
import fi.metatavu.edelphi.smvcj.controllers.PageController;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestController;
import fi.metatavu.edelphi.smvcj.controllers.RequestControllerMapper;
import fi.metatavu.edelphi.smvcj.logging.Logging;

/** The servlet responsible for processing SMVCJ application requests.
 *
 */
public class Servlet extends HttpServlet {

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    loginUrl = config.getInitParameter("loginUrl");
    if (!StringUtils.isBlank(loginUrl)) {
      if ("true".equals(config.getInitParameter("loginUrlRelative"))) {
        loginUrl = config.getServletContext().getContextPath() + "/" + loginUrl;
      }
    }
    
    decodeGETUtf = "true".equals(config.getInitParameter("decodeGETUtf"));
    
    String requestDispatcherClass = config.getInitParameter("requestDispatcher");
    if (requestDispatcherClass != null) {
      try {
        requestDispatcher = (RequestDispatcher) Class.forName(requestDispatcherClass).newInstance();
      }
      catch (Exception e) {
        Logging.logException(e);
        throw new ServletException("Failed to instantiate request dispatcher " + requestDispatcherClass, e);
      }
    }
    
    String platformErrorListenerClass = config.getInitParameter("platformErrorListener");
    if (platformErrorListenerClass != null) {
      try {
        platformErrorListener = (PlatformErrorListener) Class.forName(platformErrorListenerClass).newInstance();
      } catch (Exception e) {
        Logging.logException(e);
        throw new ServletException("Failed to instantiate platform error listener " + platformErrorListenerClass, e);
      }
    }
    
    errorJspPage = config.getInitParameter("errorJspPage");
    applicationPath = config.getInitParameter("applicationPath");
  }

  /**
   * Processes all application requests, delegating them to their corresponding page, binary and JSON controllers.
   */
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
    doService(request, response);
  }
  
  private void doService(HttpServletRequest request,
      HttpServletResponse response) throws ServletException {
    // Start a transaction for the request

    try {
      userTransaction.begin();
    } catch (Exception e) {
      Logging.logException(e);
      throw new ServletException(e);
    }

    RequestContext requestContext = null;
    RequestController requestController;
    RequestDispatchContext dispatchContext; 
    if (requestDispatcher != null && requestDispatcher.canHandle(request, response)) {
      dispatchContext = requestDispatcher.getContext(request, response);
      requestController = dispatchContext.getRequestController();
    }
    else {
      String uri = request.getRequestURI();
      String ctxPath = request.getContextPath();
      String controllerName = uri.substring(ctxPath.length() + 1);
      if (StringUtils.isNotBlank(applicationPath)) {
        controllerName = controllerName.substring(applicationPath.length());
      }
      requestController = RequestControllerMapper.getRequestController(controllerName);
      dispatchContext = new RequestDispatchContext(requestController, new DefaultParameterHandlerImpl(request, decodeGETUtf));
    }
    
    int statusCode = StatusCode.OK;
    try {

      // Determine suitable request context based on the controller type

      if (requestController == null) {
        requestContext = new PageRequestContext(dispatchContext, request, response, getServletContext(), errorJspPage);
        throw new PageNotFoundException(request.getLocale());
      }
      else if (requestController instanceof PageController) {
        requestContext = new PageRequestContext(dispatchContext, request, response, getServletContext(), errorJspPage);
      }
      else if (requestController instanceof JSONRequestController) {
        requestContext = new JSONRequestContext(dispatchContext, request, response, getServletContext());
      }
      else if (requestController instanceof BinaryRequestController) {
        requestContext = new BinaryRequestContext(dispatchContext, request, response, getServletContext());
      }

      if (requestContext == null) {
        throw new SmvcRuntimeException(500, "Failed to create request context for " + requestController.getClass().getName());
      }

      // Let the controller authorize the request. Most common exceptions thrown include
      // LoginRequiredException and AccessDeniedException 
      
      requestController.authorize(requestContext);
      requestController.beforeProcess(requestContext);
      
      // Request has been authorized, so the controller is asked to process it

      if (requestController.isSynchronous()) {
        processRequestSynchronized(requestController, requestContext);
      } else {
        processRequest(requestController, requestContext);
      }
    }
    catch (LoginRequiredException lre) {
      if (platformErrorListener != null)
        platformErrorListener.onLoginRequiredException(request, response, lre);
      
      Logging.logInfo("Login required for " + getCurrentUrl(request, true));
      if (requestController instanceof PageController) {
        HttpSession session = requestContext.getRequest().getSession(true);
        session.setAttribute("loginRedirectUrl", lre.getRedirectUrl());
        if (lre.getContextType() != null && lre.getContextId() != null) {
          session.setAttribute("loginContextType", lre.getContextType());
          session.setAttribute("loginContextId", lre.getContextId());
        }
        requestContext.setRedirectURL(loginUrl);
      }
      else {
        // TODO LoginRequiredException for requests other than pages?
        statusCode = lre.getStatusCode();
        requestContext.addMessage(Severity.WARNING, lre.getMessage());
      }
    }
    catch (PageNotFoundException pnfe) {
      if (platformErrorListener != null)
        platformErrorListener.onPageNotFoundException(request, response, pnfe);

      Logging.logInfo("404 - " + getCurrentUrl(request, true));
      statusCode = pnfe.getStatusCode();
      if (requestContext != null) {
        requestContext.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
        requestContext.addMessage(Severity.WARNING, pnfe.getMessage());
      } else {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
    }
    catch (AccessDeniedException ade) {
      if (platformErrorListener != null)
        platformErrorListener.onAccessDeniedException(request, response, ade);

      Logging.logInfo("403 - " + getCurrentUrl(request, true) + " - " + requestContext.getLoggedUserId());
      statusCode = ade.getStatusCode();
      requestContext.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
      requestContext.addMessage(Severity.WARNING, ade.getMessage());
    }
    catch (SmvcRuntimeException pre) {
      if (platformErrorListener != null)
        platformErrorListener.onSmvcRuntimeException(request, response, pre);

      Logging.logException(pre);
      statusCode = pre.getStatusCode();
      requestContext.addMessage(Severity.ERROR, pre.getMessage());
    }
    catch (Exception e) {
      if (platformErrorListener != null)
        platformErrorListener.onUncontrolledException(request, response, e);

      // All other exceptions are considered to be fatal and unexpected, so the request
      // transaction is rolled back, the stack trace of the exception is printed out, and
      // an error view is shown

      Logging.logException(e);
      statusCode = StatusCode.UNDEFINED;
      requestContext.addMessage(Severity.CRITICAL, e.getMessage());
    }
    finally {
      try {

        // Pre-commit response

        requestContext.writePreCommitResponse(statusCode);

        // Request complete, so commit or rollback based on the status of the request
        
        if (statusCode == StatusCode.OK) {
          userTransaction.commit();
        } else {
          userTransaction.rollback();
        }

        // Post-commit response

        requestContext.writePostCommitResponse(statusCode);
      }
      catch (Exception e) {
        if (platformErrorListener != null)
          platformErrorListener.onTransactionCommitException(request, response, e);

        Logging.logException(e);
        throw new ServletException(e);
      }
    }
  }

  /**
   * Process the request by invoking the appropriate method on the controller. This method is
   * synchronized to ensure that only one request is processed at a time.
   *
   * @param requestController The request controller
   * @param requestContext The request context
   */
  private synchronized void processRequestSynchronized(RequestController requestController, RequestContext requestContext) {
    processRequest(requestController, requestContext);
  }

  /**
   * Process the request by invoking the appropriate method on the controller
   *
   * @param requestController The request controller
   * @param requestContext The request context
   */
  private void processRequest(RequestController requestController, RequestContext requestContext) {
    if (requestController instanceof PageController) {
      ((PageController) requestController).process((PageRequestContext) requestContext);
    }
    else if (requestController instanceof JSONRequestController) {
      ((JSONRequestController) requestController).process((JSONRequestContext) requestContext);
    }
    else if (requestController instanceof BinaryRequestController) {
      ((BinaryRequestController) requestController).process((BinaryRequestContext) requestContext);
    }
  }

  private String getBaseUrl(HttpServletRequest request) {
    String currentURL = request.getRequestURL().toString();
    String pathInfo = request.getRequestURI();
    return currentURL.substring(0, currentURL.length() - pathInfo.length()); 
  }

  private String getCurrentUrl(HttpServletRequest request, boolean stripApp) {
    if (!stripApp) {
      StringBuilder currentUrl = new StringBuilder(request.getRequestURL());
      String queryString = request.getQueryString();
      if (!StringUtils.isBlank(queryString)) {
        currentUrl.append('?');
        currentUrl.append(queryString);
      }
      return currentUrl.toString();
    } else {
      StringBuilder currentUrl = new StringBuilder(getBaseUrl(request));
      String contextPath = request.getContextPath();
      currentUrl.append(contextPath);
      String pathInfo = request.getRequestURI().substring(contextPath.length());
      if (applicationPath != null && pathInfo.startsWith(applicationPath)) {
        pathInfo = pathInfo.substring(applicationPath.length());
      }
      currentUrl.append(pathInfo);      
      String queryString = request.getQueryString();
      if (!StringUtils.isBlank(queryString)) {
        currentUrl.append('?');
        currentUrl.append(queryString);
      }
      return currentUrl.toString();
    }
  }

  private boolean decodeGETUtf = false;
  private String loginUrl = "";
  private String errorJspPage = "";
  private String applicationPath = "";
  private RequestDispatcher requestDispatcher;
  private PlatformErrorListener platformErrorListener;
  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unused")
  @Resource
  private UserTransaction userTransaction;
  
}