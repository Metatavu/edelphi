import { ErrorResponse } from '../model/errorResponse';
import { QueryQuestionCategory } from '../model/queryQuestionCategory';
import * as URI from "urijs";
import { ApiUtils } from "./api";

export class QueryQuestionCategoriesService {

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
  public createQueryQuestionCategory(body: QueryQuestionCategory, panelId: number, ):Promise<QueryQuestionCategory> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionCategories`);
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
  public deleteQueryQuestionCategory(panelId: number, categoryId: number, ):Promise<any> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionCategories/${encodeURIComponent(String(categoryId))}`);
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
  public findQueryQuestionCategory(panelId: number, categoryId: number, ):Promise<QueryQuestionCategory> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionCategories/${encodeURIComponent(String(categoryId))}`);
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
  */
  public listQueryQuestionCategories(panelId: number, pageId?: number, ):Promise<Array<QueryQuestionCategory>> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionCategories`);
    if (pageId !== undefined && pageId !== null) {
        uri.addQuery('pageId', <any>pageId);
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
  public updateQueryQuestionCategory(body: QueryQuestionCategory, panelId: number, categoryId: number, ):Promise<QueryQuestionCategory> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryQuestionCategories/${encodeURIComponent(String(categoryId))}`);
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