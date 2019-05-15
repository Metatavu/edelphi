import { ErrorResponse } from '../model/errorResponse';
import { QueryQuestionComment } from '../model/queryQuestionComment';
import * as URI from "urijs";
import { ApiUtils } from "./api";

export class QueryQuestionCommentsService {

  private token: string;
  private basePath: string;

  constructor(basePath: string, token: string) {
    this.token = token;
    this.basePath = basePath;
  }


  /**
   * Creates query question comment
   * @summary Create query question comment
   * @param body Payload
   * @param panelId panel id
  */
  public createQueryQuestionComment(body: QueryQuestionComment, panelId: number, ):Promise<QueryQuestionComment> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionComments`);
    const options = {
      method: "post",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${this.token}`
      },
      body: JSON.stringify(body)
    };

    return fetch(uri.toString(), options).then((response) => {
      return ApiUtils.handleResponse(response);
    });
  }


  /**
   * Deletes query question comment
   * @summary Delete query question comment
   * @param panelId panel id
   * @param commentId query question comment id
  */
  public deleteQueryQuestionComment(panelId: number, commentId: number, ):Promise<any> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionComments/${encodeURIComponent(String(commentId))}`);
    const options = {
      method: "delete",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${this.token}`
      }
    };

    return fetch(uri.toString(), options).then((response) => {
      return ApiUtils.handleResponse(response);
    });
  }


  /**
   * Finds query question comment by id
   * @summary Find query question comment
   * @param panelId panel id
   * @param commentId query question comment id
  */
  public findQueryQuestionComment(panelId: number, commentId: number, ):Promise<QueryQuestionComment> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionComments/${encodeURIComponent(String(commentId))}`);
    const options = {
      method: "get",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${this.token}`
      }
    };

    return fetch(uri.toString(), options).then((response) => {
      return ApiUtils.handleResponse(response);
    });
  }


  /**
   * Lists query question comments
   * @summary Lists query question comments
   * @param panelId panel id
   * @param queryId Filter by query id
   * @param pageId Filter by query page id
   * @param userId Filter by user id
   * @param stampId Filter by stamp id. Defaults to current stamp
   * @param parentId parent comment id. With zero only root comments are returned
   * @param categoryId category id. If zero is specified only non categorized comments are returned
  */
  public listQueryQuestionComments(panelId: number, queryId?: number, pageId?: number, userId?: string, stampId?: number, parentId?: number, categoryId?: number, ):Promise<Array<QueryQuestionComment>> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionComments`);
    if (queryId !== undefined && queryId !== null) {
        uri.addQuery('queryId', <any>queryId);
    }
    if (pageId !== undefined && pageId !== null) {
        uri.addQuery('pageId', <any>pageId);
    }
    if (userId !== undefined && userId !== null) {
        uri.addQuery('userId', <any>userId);
    }
    if (stampId !== undefined && stampId !== null) {
        uri.addQuery('stampId', <any>stampId);
    }
    if (parentId !== undefined && parentId !== null) {
        uri.addQuery('parentId', <any>parentId);
    }
    if (categoryId !== undefined && categoryId !== null) {
        uri.addQuery('categoryId', <any>categoryId);
    }
    const options = {
      method: "get",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${this.token}`
      }
    };

    return fetch(uri.toString(), options).then((response) => {
      return ApiUtils.handleResponse(response);
    });
  }


  /**
   * Updates query question comment
   * @summary Update query question comment
   * @param body Payload
   * @param panelId panel id
   * @param commentId query question comment id
  */
  public updateQueryQuestionComment(body: QueryQuestionComment, panelId: number, commentId: number, ):Promise<QueryQuestionComment> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionComments/${encodeURIComponent(String(commentId))}`);
    const options = {
      method: "put",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${this.token}`
      },
      body: JSON.stringify(body)
    };

    return fetch(uri.toString(), options).then((response) => {
      return ApiUtils.handleResponse(response);
    });
  }

}