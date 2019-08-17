import { Query } from '../model/query';
export declare class QueriesService {
    private token;
    private basePath;
    constructor(basePath: string, token: string);
    /**
     * Lists queries in a panel
     * @summary Lists queries in a panel.
     * @param panelId panel id
    */
    listQueries(panelId: number): Promise<Array<Query>>;
}
