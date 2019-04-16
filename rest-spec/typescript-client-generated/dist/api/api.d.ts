export * from './queryQuestionAnswers.service';
import { QueryQuestionAnswersService } from './queryQuestionAnswers.service';
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
    getQueryQuestionAnswersService(token: string): QueryQuestionAnswersService;
    getQueryQuestionCommentsService(token: string): QueryQuestionCommentsService;
};
export default _default;
