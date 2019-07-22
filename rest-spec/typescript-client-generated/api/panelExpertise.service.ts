import { ErrorResponse } from '../model/errorResponse';
import { PanelExpertiseClass } from '../model/panelExpertiseClass';
import { PanelExpertiseGroup } from '../model/panelExpertiseGroup';
import { PanelInterestClass } from '../model/panelInterestClass';
import * as URI from "urijs";
import { ApiUtils } from "./api";

export class PanelExpertiseService {

  private token: string;
  private basePath: string;

  constructor(basePath: string, token: string) {
    this.token = token;
    this.basePath = basePath;
  }


  /**
   * List defined expertise classes from a panel
   * @summary List panel expertise classes
   * @param panelId panel id
  */
  public listExpertiseClasses(panelId: number, ):Promise<Array<PanelExpertiseClass>> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/expertiseClasses`);
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
   * List defined expertise groups from a panel
   * @summary List panel expertise groups
   * @param panelId panel id
  */
  public listExpertiseGroups(panelId: number, ):Promise<Array<PanelExpertiseGroup>> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/expertiseGroups`);
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
   * List defined interest classes from a panel
   * @summary List panel interest classes
   * @param panelId panel id
  */
  public listInterestClasses(panelId: number, ):Promise<Array<PanelInterestClass>> {
    const uri = new URI(`${this.basePath}/panels/${encodeURIComponent(String(panelId))}/interestClasses`);
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