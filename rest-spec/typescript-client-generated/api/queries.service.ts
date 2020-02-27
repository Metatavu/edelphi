import { ErrorResponse } from '../model/errorResponse';
import { Query } from '../model/query';
import { QueryQuestionComment } from '../model/queryQuestionComment';
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
   * Creates copy of an query
   * @summary Create copy of an query
   * @param panelId panel id
   * @param queryId panel id
   * @param targetPanelId target panel panel id
   * @param copyData whether to copy query data
   * @param newName new name for query copy
  */
  public copyQuery(panelId: number, queryId: number, targetPanelId: number, copyData: boolean, newName: string, ):Promise<QueryQuestionComment> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/queries/${encodeURIComponent(String(queryId))}/copy`);
    if (targetPanelId !== undefined && targetPanelId !== null) {
        uri.addQuery('targetPanelId', <any>targetPanelId);
    }
    if (copyData !== undefined && copyData !== null) {
        uri.addQuery('copyData', <any>copyData);
    }
    if (newName !== undefined && newName !== null) {
        uri.addQuery('newName', <any>newName);
    }
    const options = {
      method: "post",
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