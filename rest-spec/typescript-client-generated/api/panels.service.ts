import { ErrorResponse } from '../model/errorResponse';
import { Panel } from '../model/panel';
import * as URI from "urijs";
import { ApiUtils } from "./api";

export class PanelsService {

  private token: string;
  private basePath: string;

  constructor(basePath: string, token: string) {
    this.token = token;
    this.basePath = basePath;
  }


  /**
   * Finds a panel by id
   * @summary Find a panel.
   * @param panelId panel id
  */
  public findPanel(panelId: number, ):Promise<Panel> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}`);
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