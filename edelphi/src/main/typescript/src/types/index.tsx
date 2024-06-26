
/**
 * Redux store state
 */
export interface StoreState {
  accessToken?: AccessToken,
  locale: string,
  queryValidationMessage: string | null
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
export type Command = "edit-page-comment-options" | "edit-page-live2d-options" | "enable-query-save" | "disable-query-save" | "open-anonymous-login-dialog" | "open-copy-query-dialog" | "open-delete-panel-dialog";

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
  reason?: "noAnswer" | "replyEditNotAllowed"
} 

export interface EnableQueryNextCommandEventData {
  
} 

/**
 *  Event data for open anonymous login dialog event
 */
export interface EditQueryOpenAnonymousLoginDialogEventData {
}

/**
 *  Event data for open copy dialog dialog event
 */
export interface EditQueryOpenCopyQueryDialogEventData {
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
  command: "disable-query-save",
  data: DisableQueryNextCommandEventData
}

/**
 * Command detail for enable query event
 */
export interface EnableQueryNextCommandEventDetail {
  command: "enable-query-save",
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
 * Command payload for open anonymous login dialog event
 */
export interface EditQueryOpenCopyQueryDialogEventDetail {
  command: "open-copy-query-dialog",
  data: EditQueryOpenCopyQueryDialogEventData
}

/**
 * Command payload for open anonymous login dialog event
 */
export interface OpenDeletePanelDialogEventDetail {
  command: "open-delete-panel-dialog",
  data: {}
}

/**
 * Command event for save query answers event
 */
export interface CommandEvent extends CustomEvent {
  detail: DisableQueryNextCommandEventDetail | EnableQueryNextCommandEventDetail | EditQueryCommentOptionsEventDataDetail | EditPageCommentOptionsEventDataDetail | EditPageLive2dOptionsEventDataDetail | RemoveQueryAnswersEventDataDetail | EditQueryOpenAnonymousLoginDialogEventDetail | EditQueryOpenCopyQueryDialogEventDetail | OpenDeletePanelDialogEventDetail;
} 