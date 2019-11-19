import * as constants from '../constants';
import { AccessToken } from '../types';

/**
 * Access token update data
 */
export interface AccessTokenUpdate {
  type: constants.ACCESS_TOKEN_UPDATE,
  accessToken: AccessToken
}

/**
 * Query validation message update
 */
export interface QueryValidationMessageUpdate {
  type: constants.QUERY_VALIDATION_MESSAGE_UPDATE,
  queryValidationMessage: string | null
}

/**
 * Actions
 */
export type AppAction =  AccessTokenUpdate | QueryValidationMessageUpdate;

/**
 * Store update method for access token
 * 
 * @param accessToken access token
 */
export function accessTokenUpdate(accessToken: AccessToken): AccessTokenUpdate {
  return {
    type: constants.ACCESS_TOKEN_UPDATE,
    accessToken: accessToken
  }
}

/**
 * Store update method for query validation message
 * 
 * @param queryValidationMessage query validation message
 */
export function queryValidationMessageUpdate(queryValidationMessage: string | null): QueryValidationMessageUpdate {
  return {
    type: constants.QUERY_VALIDATION_MESSAGE_UPDATE,
    queryValidationMessage: queryValidationMessage
  };
}