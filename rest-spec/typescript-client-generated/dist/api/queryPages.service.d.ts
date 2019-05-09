import { QueryPage } from '../model/queryPage';
export declare class QueryPagesService {
    private token;
    private basePath;
    constructor(basePath: string, token: string);
    /**
     * Finds query page by id
     * @summary Find query page.
     * @param panelId panel id
     * @param queryPageId query page id
    */
    findQueryPage(panelId: number, queryPageId: number): Promise<QueryPage>;
    /**
     * Updates query page
     * @summary Update query page
     * @param body Payload
     * @param panelId panel id
     * @param queryPageId query page id
    */
    updateQueryPage(body: QueryPage, panelId: number, queryPageId: number): Promise<QueryPage>;
}
