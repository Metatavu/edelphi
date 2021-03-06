/* tslint:disable */
/* eslint-disable */
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
import {
    PanelInvitationState,
    PanelInvitationStateFromJSON,
    PanelInvitationStateFromJSONTyped,
    PanelInvitationStateToJSON,
} from './';

/**
 * 
 * @export
 * @interface PanelInvitation
 */
export interface PanelInvitation {
    /**
     * Invitation id
     * @type {number}
     * @memberof PanelInvitation
     */
    readonly id?: number;
    /**
     * Panel id
     * @type {number}
     * @memberof PanelInvitation
     */
    panelId?: number;
    /**
     * Invitation's target query id
     * @type {number}
     * @memberof PanelInvitation
     */
    queryId?: number;
    /**
     * Invitation email
     * @type {string}
     * @memberof PanelInvitation
     */
    email: string;
    /**
     * 
     * @type {PanelInvitationState}
     * @memberof PanelInvitation
     */
    state: PanelInvitationState;
    /**
     * Comment's creator id
     * @type {string}
     * @memberof PanelInvitation
     */
    readonly creatorId?: string;
    /**
     * Comment's last modifier id
     * @type {string}
     * @memberof PanelInvitation
     */
    readonly lastModifierId?: string;
    /**
     * Comment's creation time
     * @type {Date}
     * @memberof PanelInvitation
     */
    readonly created?: Date;
    /**
     * Comment's last modification time
     * @type {Date}
     * @memberof PanelInvitation
     */
    readonly lastModified?: Date;
}

export function PanelInvitationFromJSON(json: any): PanelInvitation {
    return PanelInvitationFromJSONTyped(json, false);
}

export function PanelInvitationFromJSONTyped(json: any, ignoreDiscriminator: boolean): PanelInvitation {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'panelId': !exists(json, 'panelId') ? undefined : json['panelId'],
        'queryId': !exists(json, 'queryId') ? undefined : json['queryId'],
        'email': json['email'],
        'state': PanelInvitationStateFromJSON(json['state']),
        'creatorId': !exists(json, 'creatorId') ? undefined : json['creatorId'],
        'lastModifierId': !exists(json, 'lastModifierId') ? undefined : json['lastModifierId'],
        'created': !exists(json, 'created') ? undefined : (new Date(json['created'])),
        'lastModified': !exists(json, 'lastModified') ? undefined : (new Date(json['lastModified'])),
    };
}

export function PanelInvitationToJSON(value?: PanelInvitation | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'panelId': value.panelId,
        'queryId': value.queryId,
        'email': value.email,
        'state': PanelInvitationStateToJSON(value.state),
    };
}


