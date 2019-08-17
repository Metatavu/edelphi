import { PanelExpertiseClass } from '../model/panelExpertiseClass';
import { PanelExpertiseGroup } from '../model/panelExpertiseGroup';
import { PanelInterestClass } from '../model/panelInterestClass';
export declare class PanelExpertiseService {
    private token;
    private basePath;
    constructor(basePath: string, token: string);
    /**
     * List defined expertise classes from a panel
     * @summary List panel expertise classes
     * @param panelId panel id
    */
    listExpertiseClasses(panelId: number): Promise<Array<PanelExpertiseClass>>;
    /**
     * List defined expertise groups from a panel
     * @summary List panel expertise groups
     * @param panelId panel id
    */
    listExpertiseGroups(panelId: number): Promise<Array<PanelExpertiseGroup>>;
    /**
     * List defined interest classes from a panel
     * @summary List panel interest classes
     * @param panelId panel id
    */
    listInterestClasses(panelId: number): Promise<Array<PanelInterestClass>>;
}
