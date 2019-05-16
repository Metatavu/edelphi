import { ErrorResponse } from '../model/errorResponse';
import { QueryPage } from '../model/queryPage';
import * as URI from "urijs";
import { ApiUtils } from "./api";

export class QueryPagesService {

  private token: string;
  private basePath: string;

  constructor(basePath: string, token: string) {
    this.token = token;
    this.basePath = basePath;
  }


  /**
   * Finds query page by id
   * @summary Find query page.
   * @param panelId panel id
   * @param queryPageId query page id
  */
  public findQueryPage(panelId: number, queryPageId: number, ):Promise<QueryPage> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryPages/${encodeURIComponent(String(queryPageId))}`);
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
   * Lists query pages
   * @summary Lists query pages.
   * @param panelId panel id
   * @param queryId query id
  */
  public listQueryPages(panelId: number, queryId?: number, ):Promise<Array<QueryPage>> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryPages`);
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
   * Updates query page
   * @summary Update query page
   * @param body Payload
   * @param panelId panel id
   * @param queryPageId query page id
  */
  public updateQueryPage(body: QueryPage, panelId: number, queryPageId: number, ):Promise<QueryPage> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queryPages/${encodeURIComponent(String(queryPageId))}`);
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