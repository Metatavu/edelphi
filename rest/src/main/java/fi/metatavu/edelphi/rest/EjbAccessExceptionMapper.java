package fi.metatavu.edelphi.rest;

import javax.ejb.EJBAccessException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for EJBAccessExceptions
 * 
 * @author Heikki Kurhinen
 */
@Provider
public class EjbAccessExceptionMapper implements ExceptionMapper<EJBAccessException> {

  @Override
  public Response toResponse(EJBAccessException exception) {
    return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
  }

}