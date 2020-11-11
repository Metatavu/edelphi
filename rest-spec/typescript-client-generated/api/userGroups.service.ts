import { ErrorResponse } from '../model/errorResponse';
import { PanelUserGroup } from '../model/panelUserGroup';
import * as URI from "urijs";
import { ApiUtils } from "./api";

export class UserGroupsService {

  private token: string;
  private basePath: string;

  constructor(basePath: string, token: string) {
    this.token = token;
    this.basePath = basePath;
  }


  /**
   * List defined user groups from a panel
   * @summary List panel user groups
   * @param panelId panel id
  */
  public listUserGroups(panelId: number, ):Promise<Array<PanelUserGroup>> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/userGroups`);
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