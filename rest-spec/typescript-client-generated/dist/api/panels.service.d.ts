import { Panel } from '../model/panel';
export declare class PanelsService {
    private token;
    private basePath;
    constructor(basePath: string, token: string);
    /**
     * Finds a panel by id
     * @summary Find a panel.
     * @param panelId panel id
    */
    findPanel(panelId: number): Promise<Panel>;
    /**
     * Lists panels
     * @summary Lists panels
     * @param managedOnly Return only panels user has manager access
    */
    listPanels(managedOnly?: boolean): Promise<Array<Panel>>;
}
