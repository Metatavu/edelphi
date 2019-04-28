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

}