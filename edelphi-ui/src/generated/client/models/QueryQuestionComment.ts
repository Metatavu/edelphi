// tslint:disable
// eslint-disable
/**
 * eDelphi REST API
 * REST API for eDelphi
 *
 * The version of the OpenAPI document: 1.0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { exists, mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface QueryQuestionComment
 */
export interface QueryQuestionComment {
    /**
     * Comment\'s id
     * @type {number}
     * @memberof QueryQuestionComment
     */
    readonly id?: number;
    /**
     * Comment\'s category id
     * @type {number}
     * @memberof QueryQuestionComment
     */
    category_id?: number;
    /**
     * Parent comment\'s id
     * @type {number}
     * @memberof QueryQuestionComment
     */
    parent_id?: number;
    /**
     * Whether the comment has been hided by the manager
     * @type {boolean}
     * @memberof QueryQuestionComment
     */
    hidden?: boolean;
    /**
     * Page\'s id where the comment is
     * @type {number}
     * @memberof QueryQuestionComment
     */
    query_page_id: number;
    /**
     * Comment\'s query reply id
     * @type {number}
     * @memberof QueryQuestionComment
     */
    query_reply_id: number;
    /**
     * Comment\'s contents
     * @type {string}
     * @memberof QueryQuestionComment
     */
    contents?: string;
    /**
     * Comment\'s creator id
     * @type {string}
     * @memberof QueryQuestionComment
     */
    readonly creator_id?: string;
    /**
     * Comment\'s last modifier id
     * @type {string}
     * @memberof QueryQuestionComment
     */
    readonly last_modifier_id?: string;
    /**
     * Comment\'s creation time
     * @type {Date}
     * @memberof QueryQuestionComment
     */
    readonly created?: Date;
    /**
     * Comment\'s last modification time
     * @type {Date}
     * @memberof QueryQuestionComment
     */
    readonly last_modified?: Date;
}

export function QueryQuestionCommentFromJSON(json: any): QueryQuestionComment {
    return QueryQuestionCommentFromJSONTyped(json, false);
}

export function QueryQuestionCommentFromJSONTyped(json: any, ignoreDiscriminator: boolean): QueryQuestionComment {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'category_id': !exists(json, 'categoryId') ? undefined : json['categoryId'],
        'parent_id': !exists(json, 'parentId') ? undefined : json['parentId'],
        'hidden': !exists(json, 'hidden') ? undefined : json['hidden'],
        'query_page_id': json['queryPageId'],
        'query_reply_id': json['queryReplyId'],
        'contents': !exists(json, 'contents') ? undefined : json['contents'],
        'creator_id': !exists(json, 'creatorId') ? undefined : json['creatorId'],
        'last_modifier_id': !exists(json, 'lastModifierId') ? undefined : json['lastModifierId'],
        'created': !exists(json, 'created') ? undefined : (new Date(json['created'])),
        'last_modified': !exists(json, 'lastModified') ? undefined : (new Date(json['lastModified'])),
    };
}

export function QueryQuestionCommentToJSON(value?: QueryQuestionComment | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'categoryId': value.category_id,
        'parentId': value.parent_id,
        'hidden': value.hidden,
        'queryPageId': value.query_page_id,
        'queryReplyId': value.query_reply_id,
        'contents': value.contents,
    };
}


