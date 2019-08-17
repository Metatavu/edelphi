import { ErrorResponse } from '../model/errorResponse';
import { ReportRequest } from '../model/reportRequest';
import * as URI from "urijs";
import { ApiUtils } from "./api";

export class ReportsService {

  private token: string;
  private basePath: string;

  constructor(basePath: string, token: string) {
    this.token = token;
    this.basePath = basePath;
  }


  /**
   * Creates a request to generate a report
   * @summary Creates a report request
   * @param body Payload
  */
  public createReportRequest(body: ReportRequest, ):Promise<any> {
    const uri = new URI(`${this.basePath}/reportRequests`);
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

}