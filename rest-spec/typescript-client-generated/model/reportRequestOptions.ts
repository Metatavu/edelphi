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


export interface ReportRequestOptions { 
    /**
     * Include only speficied page ids
     */
    queryPageIds?: Array<number>;
    /**
     * Include only speficied expertise group ids
     */
    expertiseGroupIds?: Array<number>;
    /**
     * Include only speficied panel user group ids
     */
    panelUserGroupIds?: Array<number>;
    /**
     * Include only comments from speficied comment category ids
     */
    commentCategoryIds?: Array<number>;
    /**
     * Show 2d answers as 1d graphs instead of 2d graphs
     */
    show2dAs1d?: boolean;
}
export interface ReportRequestOptionsOpt { 
    /**
     * Include only speficied page ids
     */
    queryPageIds?: Array<number>;
    /**
     * Include only speficied expertise group ids
     */
    expertiseGroupIds?: Array<number>;
    /**
     * Include only speficied panel user group ids
     */
    panelUserGroupIds?: Array<number>;
    /**
     * Include only comments from speficied comment category ids
     */
    commentCategoryIds?: Array<number>;
    /**
     * Show 2d answers as 1d graphs instead of 2d graphs
     */
    show2dAs1d?: boolean;
}
