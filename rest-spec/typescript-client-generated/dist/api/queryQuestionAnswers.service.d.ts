import { QueryQuestionAnswer } from '../model/queryQuestionAnswer';
export declare class QueryQuestionAnswersService {
    private token;
    private basePath;
    constructor(basePath: string, token: string);
    /**
     * Deletes query question answers
     * @summary Delete query question answers
     * @param panelId panel id
     * @param queryId Delete answers by query
     * @param queryPageId Delete answers by query page
     * @param queryReplyId Delete answers by query reply
    */
    deleteQueryQuestionAnswers(panelId: number, queryId?: number, queryPageId?: number, queryReplyId?: number): Promise<any>;
    /**
     * Finds query question answer by id
     * @summary Find query question answer.
     * @param panelId panel id
     * @param answerId query question answer id
    */
    findQueryQuestionAnswer(panelId: number, answerId: string): Promise<QueryQuestionAnswer>;
    /**
     * Lists query question answers
     * @summary Lists query question answers
     * @param panelId panel id
     * @param queryId Filter by query id
     * @param pageId Filter by query page id
     * @param userId Filter by user id
     * @param stampId Filter by stamp id. Defaults to current stamp
    */
    listQueryQuestionAnswers(panelId: number, queryId?: number, pageId?: number, userId?: string, stampId?: number): Promise<Array<QueryQuestionAnswer>>;
    /**
     * Creates or updates query question answer
     * @summary Creates or updates query question answer
     * @param body Payload
     * @param panelId panel id
     * @param answerId query question answer id
    */
    upsertQueryQuestionAnswer(body: QueryQuestionAnswer, panelId: number, answerId: string): Promise<QueryQuestionAnswer>;
}
