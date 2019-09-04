import { ErrorResponse } from '../model/errorResponse';
import { QueryQuestionCommentCategory } from '../model/queryQuestionCommentCategory';
import * as URI from "urijs";
import { ApiUtils } from "./api";

export class QueryQuestionCommentCategoriesService {

  private token: string;
  private basePath: string;

  constructor(basePath: string, token: string) {
    this.token = token;
    this.basePath = basePath;
  }


  /**
   * Creates query question category
   * @summary Create query question category
   * @param body Payload
   * @param panelId panel id
  */
  public createQueryQuestionCommentCategory(body: QueryQuestionCommentCategory, panelId: number, ):Promise<QueryQuestionCommentCategory> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionCommentCategories`);
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
   * Deletes query question category
   * @summary Delete query question category
   * @param panelId panel id
   * @param categoryId query question category id
  */
  public deleteQueryQuestionCommentCategory(panelId: number, categoryId: number, ):Promise<any> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionCommentCategories/${encodeURIComponent(String(categoryId))}`);
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
   * Finds query question category by id
   * @summary Find query question category
   * @param panelId panel id
   * @param categoryId query question category id
  */
  public findQueryQuestionCommentCategory(panelId: number, categoryId: number, ):Promise<QueryQuestionCommentCategory> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionCommentCategories/${encodeURIComponent(String(categoryId))}`);
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
   * Lists query question categories
   * @summary Lists query question categories
   * @param panelId panel id
   * @param pageId Filter by query page id
   * @param queryId Filter by query id
  */
  public listQueryQuestionCommentCategories(panelId: number, pageId?: number, queryId?: number, ):Promise<Array<QueryQuestionCommentCategory>> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionCommentCategories`);
    if (pageId !== undefined && pageId !== null) {
        uri.addQuery('pageId', <any>pageId);
    }
    if (queryId !== undefined && queryId !== null) {
        uri.addQuery('queryId', <any>queryId);
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
   * Updates query question category
   * @summary Update query question category
   * @param body Payload
   * @param panelId panel id
   * @param categoryId query question category id
  */
  public updateQueryQuestionCommentCategory(body: QueryQuestionCommentCategory, panelId: number, categoryId: number, ):Promise<QueryQuestionCommentCategory> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionCommentCategories/${encodeURIComponent(String(categoryId))}`);
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