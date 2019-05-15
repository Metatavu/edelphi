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
import { QueryState } from './queryState';


export interface Query { 
    readonly id?: number;
    allowEditReply?: boolean;
    closes?: string;
    state?: QueryState;
    name?: string;
    urlName?: string;
    visible?: boolean;
    description?: string;
    /**
     * Comment's creator id
     */
    readonly creatorId?: string;
    /**
     * Comment's last modifier id
     */
    readonly lastModifierId?: string;
    /**
     * Comment's creation time
     */
    readonly created?: string;
    /**
     * Comment's last modification time
     */
    readonly lastModified?: string;
}
export interface QueryOpt { 
    readonly id?: number;
    allowEditReply?: boolean;
    closes?: string;
    state?: QueryState;
    name?: string;
    urlName?: string;
    visible?: boolean;
    description?: string;
    /**
     * Comment's creator id
     */
    readonly creatorId?: string;
    /**
     * Comment's last modifier id
     */
    readonly lastModifierId?: string;
    /**
     * Comment's creation time
     */
    readonly created?: string;
    /**
     * Comment's last modification time
     */
    readonly lastModified?: string;
}
