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
 * Actions
 */
export type AppAction =  AccessTokenUpdate;

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