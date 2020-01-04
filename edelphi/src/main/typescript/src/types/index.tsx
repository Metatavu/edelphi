
/**
 * Redux store state
 */
export interface StoreState {
  accessToken?: AccessToken,
  locale: string,
  queryValidationMessage: string | null
}

/**
 * Interface for page change event 
 */
export interface PageChangeEvent {

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
 * Interface representing a query page statistics
 */
export interface QueryPageStatistics {
  answerCount: number,
  q1: number | null,
  q2: number | null,
  q3: number | null
}

/**
 * Interface representing a single user answer 
 */
export interface QueryLive2dAnswer {
  x: number,
  y: number,
  z: number,
  id: string
}

/**
 * Command
 */
export type Command = "edit-page-comment-options" | "edit-page-live2d-options" | "enable-query-next" | "disable-query-next" | "open-anonymous-login-dialog";

/**
 * Command data for save query answers event
 */
export interface SaveQueryAnswersCommandData {
  pageType: string,
  currentPageNumber: number,
  nextPageNumber: number,
  previousPageNumber: number
}

export interface DisableQueryNextCommandEventData {
  reason?: string
} 

export interface EnableQueryNextCommandEventData {
  
} 

/**
 *  Event data for open anonymous login dialog event
 */
export interface EditQueryOpenAnonymousLoginDialogEventData {
}

export interface EditQueryCommentOptionsEventData {
  pageDatas: EditPageLegacyPageData[]
}

export interface RemoveQueryAnswersEventData {
  
}

/**
 * Command page data for legacy command
 */
export interface EditPageLegacyPageData {
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

export interface EditPageLegacyPageEventData {
  pageData: EditPageLegacyPageData
}

/**
 * Command detail for enable query event
 */
export interface DisableQueryNextCommandEventDetail {
  command: "disable-query-next",
  data: DisableQueryNextCommandEventData
}

/**
 * Command detail for enable query event
 */
export interface EnableQueryNextCommandEventDetail {
  command: "enable-query-next",
  data: EnableQueryNextCommandEventData
}

/**
 * Command detail for enable query event
 */
export interface EditQueryCommentOptionsEventDataDetail {
  command: "edit-query-comment-options",
  data: EditQueryCommentOptionsEventData
}

/**
 * Command payload for comment options command
 */
export interface EditPageCommentOptionsEventDataDetail {
  command: "edit-page-comment-options",
  data: EditPageLegacyPageEventData
}

/**
 * Command payload for live2d options command
 */
export interface EditPageLive2dOptionsEventDataDetail {
  command: "edit-page-live2d-options",
  data: EditPageLegacyPageEventData
}

/**
 * Command payload for remove query answers
 */
export interface RemoveQueryAnswersEventDataDetail {
  command: "remove-query-answers",
  data: RemoveQueryAnswersEventData
}

/**
 * Command payload for open anonymous login dialog event
 */
export interface EditQueryOpenAnonymousLoginDialogEventDetail {
  command: "open-anonymous-login-dialog",
  data: EditQueryOpenAnonymousLoginDialogEventData
}

/**
 * Command event for save query answers event
 */
export interface CommandEvent extends CustomEvent {
  detail: DisableQueryNextCommandEventDetail | EnableQueryNextCommandEventDetail | EditQueryCommentOptionsEventDataDetail | EditPageCommentOptionsEventDataDetail | EditPageLive2dOptionsEventDataDetail | RemoveQueryAnswersEventDataDetail | EditQueryOpenAnonymousLoginDialogEventDetail
} 