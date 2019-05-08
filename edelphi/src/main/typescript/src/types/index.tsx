
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

/**
 * Interface describing an query question comment notification MQTT message
 */
export interface QueryQuestionAnswerNotification {
  type: "UPDATED";
  panelId: number,
  queryId: number,
  pageId: number,
  answerId: string,
}

export type Command = "edit-page-comment-options";

export interface EditPageCommentOptionsCommand {
  type: "edit-page-comment-options",
  pageData: {
    hasAnswers: boolean,
    id: number,
    number: number,
    options: {
      caption: string,
      name: string,
      editor: string
    }[],
    title: string,
    type: string
  }
}