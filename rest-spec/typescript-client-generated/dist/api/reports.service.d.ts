import { ReportRequest } from '../model/reportRequest';
export declare class ReportsService {
    private token;
    private basePath;
    constructor(basePath: string, token: string);
    /**
     * Creates a request to generate a report
     * @summary Creates a report request
     * @param body Payload
    */
    createReportRequest(body: ReportRequest): Promise<any>;
}
