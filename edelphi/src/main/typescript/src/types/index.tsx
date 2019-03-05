
/**
 * Redux store state
 */
export interface StoreState {
  accessToken?: AccessToken,
}

/**
 * Interface describing an access token
 */
export interface AccessToken {
  token: string
  expires: Date
}