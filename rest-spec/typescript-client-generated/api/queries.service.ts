import { ErrorResponse } from '../model/errorResponse';
import { Query } from '../model/query';
import * as URI from "urijs";
import { ApiUtils } from "./api";

export class QueriesService {

  private token: string;
  private basePath: string;

  constructor(basePath: string, token: string) {
    this.token = token;
    this.basePath = basePath;
  }


  /**
   * Lists queries in a panel
   * @summary Lists queries in a panel.
   * @param panelId panel id
  */
  public listQueries(panelId: number, ):Promise<Array<Query>> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queries`);
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