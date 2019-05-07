export * from './queryPages.service';
import { QueryPagesService } from './queryPages.service';
export * from './queryQuestionAnswers.service';
import { QueryQuestionAnswersService } from './queryQuestionAnswers.service';
export * from './queryQuestionCategories.service';
import { QueryQuestionCategoriesService } from './queryQuestionCategories.service';
export * from './queryQuestionComments.service';
import { QueryQuestionCommentsService } from './queryQuestionComments.service';
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
    getQueryPagesService(token: string): QueryPagesService;
    getQueryQuestionAnswersService(token: string): QueryQuestionAnswersService;
    getQueryQuestionCategoriesService(token: string): QueryQuestionCategoriesService;
    getQueryQuestionCommentsService(token: string): QueryQuestionCommentsService;
};
export default _default;
