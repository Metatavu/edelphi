
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
  expires: Date,
  userId: string
}

/**
 * Interface describing an query question comment notification MQTT message
 */
export interface QueryQuestionCommentNotification {
  type: "CREATED" | "UPDATED" | "DELETED";
  panelId: number,
  queryId: number,
  pageId: number,
  commentId: number,
  parentCommentId: number | null,
}