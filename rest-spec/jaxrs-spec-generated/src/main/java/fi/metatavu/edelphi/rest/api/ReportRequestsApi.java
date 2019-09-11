package fi.metatavu.edelphi.rest.api;

import fi.metatavu.edelphi.rest.model.*;
import java.util.UUID;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.swagger.annotations.*;

import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path("/reportRequests")
@Api(description = "the reportRequests API")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSSpecServerCodegen", date = "2019-09-09T05:40:18.567+03:00[Europe/Helsinki]")
public interface ReportRequestsApi {

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Creates a report request", notes = "Creates a request to generate a report", authorizations = {
        @Authorization(value = "bearer")    }, tags={ "Reports" })
    @ApiResponses(value = { 
        @ApiResponse(code = 202, message = "Report request has been accepted", response = Void.class),
        @ApiResponse(code = 400, message = "Invalid request was sent to the server", response = ErrorResponse.class),
        @ApiResponse(code = 403, message = "Attempted to make a call with unauthorized client", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class) })
    Response createReportRequest(@Valid ReportRequest body);}
