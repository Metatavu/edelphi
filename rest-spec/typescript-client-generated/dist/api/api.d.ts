export * from './panels.service';
import { PanelsService } from './panels.service';
export * from './queries.service';
import { QueriesService } from './queries.service';
export * from './queryPages.service';
import { QueryPagesService } from './queryPages.service';
export * from './queryQuestionAnswers.service';
import { QueryQuestionAnswersService } from './queryQuestionAnswers.service';
export * from './queryQuestionCommentCategories.service';
import { QueryQuestionCommentCategoriesService } from './queryQuestionCommentCategories.service';
export * from './queryQuestionComments.service';
import { QueryQuestionCommentsService } from './queryQuestionComments.service';
export * from './reports.service';
import { ReportsService } from './reports.service';
export declare class ApiUtils {
    /**
     * Handles response from API
     *
     * @param response response object
     */
    static handleResponse(response: any): any;
}
declare const _default: {
    apiUrl: string;
    /**
     * Configures api endpoint
     *
     */
    configure(baseUrl: string): void;
    getPanelsService(token: string): PanelsService;
    getQueriesService(token: string): QueriesService;
    getQueryPagesService(token: string): QueryPagesService;
    getQueryQuestionAnswersService(token: string): QueryQuestionAnswersService;
    getQueryQuestionCommentCategoriesService(token: string): QueryQuestionCommentCategoriesService;
    getQueryQuestionCommentsService(token: string): QueryQuestionCommentsService;
    getReportsService(token: string): ReportsService;
};
export default _default;
