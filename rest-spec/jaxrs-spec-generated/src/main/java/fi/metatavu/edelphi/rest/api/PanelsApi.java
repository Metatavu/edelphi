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

@Path("/panels")
@Api(description = "the panels API")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSSpecServerCodegen", date = "2019-04-09T10:05:25.926+03:00[Europe/Helsinki]")
public interface PanelsApi {

    @POST
    @Path("/{panelId}/queryQuestionComments")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Create query question comment", notes = "Creates query question comment", authorizations = {
        @Authorization(value = "bearer")    }, tags={ "QueryQuestionComments" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Created query question comment", response = QueryQuestionComment.class),
        @ApiResponse(code = 400, message = "Invalid request was sent to the server", response = ErrorResponse.class),
        @ApiResponse(code = 403, message = "Attempted to make a call with unauthorized client", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class) })
    Response createQueryQuestionComment(@Valid QueryQuestionComment body,@PathParam("panelId") @ApiParam("panel id") Long panelId);
    @DELETE
    @Path("/{panelId}/queryQuestionComments/{commentId}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Delete query question comment", notes = "Deletes query question comment", authorizations = {
        @Authorization(value = "bearer")    }, tags={ "QueryQuestionComments" })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "No content to indicate successful delete", response = Void.class),
        @ApiResponse(code = 400, message = "Invalid request was sent to the server", response = ErrorResponse.class),
        @ApiResponse(code = 403, message = "Attempted to make a call with unauthorized client", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class) })
    Response deleteQueryQuestionComment(@PathParam("panelId") @ApiParam("panel id") Long panelId,@PathParam("commentId") @ApiParam("query question comment id") Long commentId);
    @GET
    @Path("/{panelId}/queryQuestionComments/{commentId}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Find query question comment", notes = "Finds query question comment by id", authorizations = {
        @Authorization(value = "bearer")    }, tags={ "QueryQuestionComments" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "A queryQuestionComment", response = QueryQuestionComment.class),
        @ApiResponse(code = 400, message = "Invalid request was sent to the server", response = ErrorResponse.class),
        @ApiResponse(code = 403, message = "Attempted to make a call with unauthorized client", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class) })
    Response findQueryQuestionComment(@PathParam("panelId") @ApiParam("panel id") Long panelId,@PathParam("commentId") @ApiParam("query question comment id") Long commentId);
    @GET
    @Path("/{panelId}/queryQuestionComments")
    @Produces({ "application/json" })
    @ApiOperation(value = "Lists query question comments", notes = "Lists query question comments", authorizations = {
        @Authorization(value = "bearer")    }, tags={ "QueryQuestionComments" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "List of query question comments", response = QueryQuestionComment.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Invalid request was sent to the server", response = ErrorResponse.class, responseContainer = "List"),
        @ApiResponse(code = 403, message = "Attempted to make a call with unauthorized client", response = ErrorResponse.class, responseContainer = "List"),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class, responseContainer = "List") })
    Response listQueryQuestionComments(@PathParam("panelId") @ApiParam("panel id") Long panelId,@QueryParam("parentId") @NotNull   @ApiParam("parent comment id. With zero only root comments are returned")  Long parentId,@QueryParam("queryId")   @ApiParam("Filter by query id")  Long queryId,@QueryParam("pageId")   @ApiParam("Filter by query page id")  Long pageId,@QueryParam("userId")   @ApiParam("Filter by user id")  UUID userId,@QueryParam("stampId")   @ApiParam("Filter by stamp id. Defaults to current stamp")  Long stampId);
    @PUT
    @Path("/{panelId}/queryQuestionComments/{commentId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Update query question comment", notes = "Updates query question comment", authorizations = {
        @Authorization(value = "bearer")    }, tags={ "QueryQuestionComments" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Updated query question comment", response = QueryQuestionComment.class),
        @ApiResponse(code = 400, message = "Invalid request was sent to the server", response = ErrorResponse.class),
        @ApiResponse(code = 403, message = "Attempted to make a call with unauthorized client", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class) })
    Response updateQueryQuestionComment(@Valid QueryQuestionComment body,@PathParam("panelId") @ApiParam("panel id") Long panelId,@PathParam("commentId") @ApiParam("query question comment id") Long commentId);}
