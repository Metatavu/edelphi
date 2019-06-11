
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

/**
 * Command
 */
export type Command = "edit-page-comment-options" | "edit-page-live2d-options" | "save-query-answers";

/**
 * Command data for save query answers event
 */
export interface SaveQueryAnswersCommandData {
  pageType: string,
  currentPageNumber: number,
  nextPageNumber: number,
  previousPageNumber: number
}

/**
 * Command detail for save query answers event
 */
export interface SaveQueryAnswersCommandEventDetail {
  command: "save-query-answers",
  data: SaveQueryAnswersCommandData
}

/**
 * Command event for save query answers event
 */
export interface SaveQueryAnswersCommandEvent extends CustomEvent {
  detail: SaveQueryAnswersCommandEventDetail
} 

/**
 * Command page data for legacy command
 */
export interface EditPageLegacyCommandPageData {
  hasAnswers: "true" | "false",
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

/**
 * Command payload for comment options command
 */
export interface EditPageCommentOptionsCommand {
  type: "edit-page-comment-options",
  pageData: EditPageLegacyCommandPageData
}

/**
 * Command payload for live2d options command
 */
export interface EditPageLive2dOptionsCommand {
  type: "edit-page-live2d-options",
  pageData: EditPageLegacyCommandPageData
}