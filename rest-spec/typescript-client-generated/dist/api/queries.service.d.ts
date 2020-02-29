import { Query } from '../model/query';
import { QueryQuestionComment } from '../model/queryQuestionComment';
export declare class QueriesService {
    private token;
    private basePath;
    constructor(basePath: string, token: string);
    /**
     * Creates copy of an query
     * @summary Create copy of an query
     * @param panelId panel id
     * @param queryId panel id
     * @param targetPanelId target panel panel id
     * @param copyData whether to copy query data
     * @param newName new name for query copy
    */
    copyQuery(panelId: number, queryId: number, targetPanelId: number, copyData: boolean, newName: string): Promise<QueryQuestionComment>;
    /**
     * Lists queries in a panel
     * @summary Lists queries in a panel.
     * @param panelId panel id
    */
    listQueries(panelId: number): Promise<Array<Query>>;
}
