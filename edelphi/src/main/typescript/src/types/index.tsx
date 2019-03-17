
/**
 * Redux store state
 */
export interface StoreState {
  accessToken?: AccessToken,
  locale: string
}

/**
 * Interface describing an access token
 */
export interface AccessToken {
  token: string
  expires: Date
}