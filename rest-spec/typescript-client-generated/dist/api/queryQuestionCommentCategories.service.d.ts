import { QueryQuestionCommentCategory } from '../model/queryQuestionCommentCategory';
export declare class QueryQuestionCommentCategoriesService {
    private token;
    private basePath;
    constructor(basePath: string, token: string);
    /**
     * Creates query question category
     * @summary Create query question category
     * @param body Payload
     * @param panelId panel id
    */
    createQueryQuestionCommentCategory(body: QueryQuestionCommentCategory, panelId: number): Promise<QueryQuestionCommentCategory>;
    /**
     * Deletes query question category
     * @summary Delete query question category
     * @param panelId panel id
     * @param categoryId query question category id
    */
    deleteQueryQuestionCommentCategory(panelId: number, categoryId: number): Promise<any>;
    /**
     * Finds query question category by id
     * @summary Find query question category
     * @param panelId panel id
     * @param categoryId query question category id
    */
    findQueryQuestionCommentCategory(panelId: number, categoryId: number): Promise<QueryQuestionCommentCategory>;
    /**
     * Lists query question categories
     * @summary Lists query question categories
     * @param panelId panel id
     * @param pageId Filter by query page id
    */
    listQueryQuestionCommentCategories(panelId: number, pageId?: number): Promise<Array<QueryQuestionCommentCategory>>;
    /**
     * Updates query question category
     * @summary Update query question category
     * @param body Payload
     * @param panelId panel id
     * @param categoryId query question category id
    */
    updateQueryQuestionCommentCategory(body: QueryQuestionCommentCategory, panelId: number, categoryId: number): Promise<QueryQuestionCommentCategory>;
}
