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
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSSpecServerCodegen", date = "2019-04-18T21:28:52.659+03:00[Europe/Helsinki]")
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
    Response createQueryQuestionComment(@Valid QueryQuestionComment body,@PathParam("panelId")  Long panelId);
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
    Response deleteQueryQuestionComment(@PathParam("panelId")  Long panelId,@PathParam("commentId")  Long commentId);
    @GET
    @Path("/{panelId}/queryPages/{queryPageId}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Find query page.", notes = "Finds query page by id", authorizations = {
        @Authorization(value = "bearer")    }, tags={ "QueryPages" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "A queryPage", response = QueryPage.class),
        @ApiResponse(code = 400, message = "Invalid request was sent to the server", response = ErrorResponse.class),
        @ApiResponse(code = 403, message = "Attempted to make a call with unauthorized client", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class) })
    Response findQueryPage(@PathParam("panelId")  Long panelId,@PathParam("queryPageId")  Long queryPageId);
    @GET
    @Path("/{panelId}/queryQuestionAnswers/{answerId}")
    @Produces({ "application/json" })
    @ApiOperation(value = "Find query question answer.", notes = "Finds query question answer by id", authorizations = {
        @Authorization(value = "bearer")    }, tags={ "QueryQuestionAnswers" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "A queryQuestionAnswer", response = QueryQuestionAnswer.class),
        @ApiResponse(code = 400, message = "Invalid request was sent to the server", response = ErrorResponse.class),
        @ApiResponse(code = 403, message = "Attempted to make a call with unauthorized client", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class) })
    Response findQueryQuestionAnswer(@PathParam("panelId")  Long panelId,@PathParam("answerId")  String answerId);
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
    Response findQueryQuestionComment(@PathParam("panelId")  Long panelId,@PathParam("commentId")  Long commentId);
    @GET
    @Path("/{panelId}/queryQuestionAnswers")
    @Produces({ "application/json" })
    @ApiOperation(value = "Lists query question answers", notes = "Lists query question answers", authorizations = {
        @Authorization(value = "bearer")    }, tags={ "QueryQuestionAnswers" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "List of query question answers", response = QueryQuestionAnswer.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Invalid request was sent to the server", response = ErrorResponse.class, responseContainer = "List"),
        @ApiResponse(code = 403, message = "Attempted to make a call with unauthorized client", response = ErrorResponse.class, responseContainer = "List"),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class, responseContainer = "List") })
    Response listQueryQuestionAnswers(@PathParam("panelId")  Long panelId,@QueryParam("queryId")     Long queryId,@QueryParam("pageId")     Long pageId,@QueryParam("userId")     UUID userId,@QueryParam("stampId")     Long stampId);
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
    Response listQueryQuestionComments(@PathParam("panelId")  Long panelId,@QueryParam("parentId") @NotNull     Long parentId,@QueryParam("queryId")     Long queryId,@QueryParam("pageId")     Long pageId,@QueryParam("userId")     UUID userId,@QueryParam("stampId")     Long stampId);
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
    Response updateQueryQuestionComment(@Valid QueryQuestionComment body,@PathParam("panelId")  Long panelId,@PathParam("commentId")  Long commentId);
    @PUT
    @Path("/{panelId}/queryQuestionAnswers/{answerId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Creates or updates query question answer", notes = "Creates or updates query question answer", authorizations = {
        @Authorization(value = "bearer")    }, tags={ "QueryQuestionAnswers" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Updated query question answer", response = QueryQuestionAnswer.class),
        @ApiResponse(code = 400, message = "Invalid request was sent to the server", response = ErrorResponse.class),
        @ApiResponse(code = 403, message = "Attempted to make a call with unauthorized client", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class) })
    Response upsertQueryQuestionAnswer(@Valid QueryQuestionAnswer body,@PathParam("panelId")  Long panelId,@PathParam("answerId")  String answerId);}
