import { ErrorResponse } from '../model/errorResponse';
import { QueryQuestionAnswer } from '../model/queryQuestionAnswer';
import * as URI from "urijs";
import { ApiUtils } from "./api";

export class QueryQuestionAnswersService {

  private token: string;
  private basePath: string;

  constructor(basePath: string, token: string) {
    this.token = token;
    this.basePath = basePath;
  }


  /**
   * Deletes query question answers
   * @summary Delete query question answers
   * @param panelId panel id
   * @param queryId Delete answers by query
   * @param queryPageId Delete answers by query page
   * @param queryReplyId Delete answers by query reply
  */
  public deleteQueryQuestionAnswers(panelId: number, queryId?: number, queryPageId?: number, queryReplyId?: number, ):Promise<any> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionAnswers`);
    if (queryId !== undefined && queryId !== null) {
        uri.addQuery('queryId', <any>queryId);
    }
    if (queryPageId !== undefined && queryPageId !== null) {
        uri.addQuery('queryPageId', <any>queryPageId);
    }
    if (queryReplyId !== undefined && queryReplyId !== null) {
        uri.addQuery('queryReplyId', <any>queryReplyId);
    }
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
   * Finds query question answer by id
   * @summary Find query question answer.
   * @param panelId panel id
   * @param answerId query question answer id
  */
  public findQueryQuestionAnswer(panelId: number, answerId: string, ):Promise<QueryQuestionAnswer> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionAnswers/${encodeURIComponent(String(answerId))}`);
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
   * Lists query question answers
   * @summary Lists query question answers
   * @param panelId panel id
   * @param queryId Filter by query id
   * @param pageId Filter by query page id
   * @param userId Filter by user id
   * @param stampId Filter by stamp id. Defaults to current stamp
  */
  public listQueryQuestionAnswers(panelId: number, queryId?: number, pageId?: number, userId?: string, stampId?: number, ):Promise<Array<QueryQuestionAnswer>> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionAnswers`);
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
   * Creates or updates query question answer
   * @summary Creates or updates query question answer
   * @param body Payload
   * @param panelId panel id
   * @param answerId query question answer id
  */
  public upsertQueryQuestionAnswer(body: QueryQuestionAnswer, panelId: number, answerId: string, ):Promise<QueryQuestionAnswer> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionAnswers/${encodeURIComponent(String(answerId))}`);
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