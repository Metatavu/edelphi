/**
 * eDelphi REST API
 * REST API for eDelphi
 *
 * OpenAPI spec version: 1.0.1
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
export interface PanelExpertiseGroup {
    /**
     * Id
     */
    readonly id?: number;
    interestClassId: number;
    expertiseClassId: number;
}
export interface PanelExpertiseGroupOpt {
    /**
     * Id
     */
    readonly id?: number;
    interestClassId?: number;
    expertiseClassId?: number;
}
