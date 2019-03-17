import { QueryQuestionComment } from '../model/queryQuestionComment';
export declare class QueryQuestionCommentsService {
    private token;
    private basePath;
    constructor(basePath: string, token: string);
    /**
     * Creates query question comment
     * @summary Create query question comment
     * @param body Payload
     * @param panelId panel id
    */
    createQueryQuestionComment(body: QueryQuestionComment, panelId: number): Promise<QueryQuestionComment>;
    /**
     * Deletes query question comment
     * @summary Delete query question comment
     * @param panelId panel id
     * @param commentId query question comment id
    */
    deleteQueryQuestionComment(panelId: number, commentId: number): Promise<any>;
    /**
     * Finds query question comment by id
     * @summary Find query question comment
     * @param panelId panel id
     * @param commentId query question comment id
    */
    findQueryQuestionComment(panelId: number, commentId: number): Promise<QueryQuestionComment>;
    /**
     * Lists query question comments
     * @summary Lists query question comments
     * @param panelId panel id
     * @param parentId parent comment id. With zero only root comments are returned
     * @param queryId Filter by query id
     * @param pageId Filter by query page id
     * @param stampId Filter by stamp id. Defaults to current stamp
    */
    listQueryQuestionComments(panelId: number, parentId: number, queryId?: number, pageId?: number, stampId?: number): Promise<Array<QueryQuestionComment>>;
    /**
     * Updates query question comment
     * @summary Update query question comment
     * @param body Payload
     * @param panelId panel id
     * @param commentId query question comment id
    */
    updateQueryQuestionComment(body: QueryQuestionComment, panelId: number, commentId: number): Promise<QueryQuestionComment>;
}
