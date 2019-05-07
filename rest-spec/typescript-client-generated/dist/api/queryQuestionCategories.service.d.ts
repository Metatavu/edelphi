import { QueryQuestionCategory } from '../model/queryQuestionCategory';
export declare class QueryQuestionCategoriesService {
    private token;
    private basePath;
    constructor(basePath: string, token: string);
    /**
     * Creates query question category
     * @summary Create query question category
     * @param body Payload
     * @param panelId panel id
    */
    createQueryQuestionCategory(body: QueryQuestionCategory, panelId: number): Promise<QueryQuestionCategory>;
    /**
     * Deletes query question category
     * @summary Delete query question category
     * @param panelId panel id
     * @param categoryId query question category id
    */
    deleteQueryQuestionCategory(panelId: number, categoryId: number): Promise<any>;
    /**
     * Finds query question category by id
     * @summary Find query question category
     * @param panelId panel id
     * @param categoryId query question category id
    */
    findQueryQuestionCategory(panelId: number, categoryId: number): Promise<QueryQuestionCategory>;
    /**
     * Lists query question categories
     * @summary Lists query question categories
     * @param panelId panel id
     * @param pageId Filter by query page id
    */
    listQueryQuestionCategories(panelId: number, pageId?: number): Promise<Array<QueryQuestionCategory>>;
    /**
     * Updates query question category
     * @summary Update query question category
     * @param body Payload
     * @param panelId panel id
     * @param categoryId query question category id
    */
    updateQueryQuestionCategory(body: QueryQuestionCategory, panelId: number, categoryId: number): Promise<QueryQuestionCategory>;
}
