import { Configuration, PanelExpertiseApi, PanelsApi, QueriesApi, QueryPagesApi, QueryQuestionAnswersApi, QueryQuestionCommentCategoriesApi, QueryQuestionCommentsApi, ReportsApi, UserGroupsApi, UsersApi } from "../generated/client";

const location = window.location;
const basePath = `${location.protocol}//${location.hostname}:${location.port}/api/v1`;
    
/**
 * Utility class for loading api with predefined configuration
 */
export default class Api {

  /**
   * Gets initialized api
   *
   * @param token access token
   * @returns initialized api
   */
  public static getQueriesApi(accessToken: string) {
    return new QueriesApi(Api.getConfiguration(accessToken));
  }

  /**
   * Gets initialized api
   *
   * @param token access token
   * @returns initialized api
   */
  public static getUsersApi(accessToken: string) {
    return new UsersApi(Api.getConfiguration(accessToken));
  }

  /**
   * Gets initialized api
   *
   * @param token access token
   * @returns initialized api
   */
  public static getUserGroupsApi(accessToken: string) {
    return new UserGroupsApi(Api.getConfiguration(accessToken));
  }

  /**
   * Gets initialized api
   *
   * @param token access token
   * @returns initialized api
   */
  public static getPanelsApi(accessToken: string) {
    return new PanelsApi(Api.getConfiguration(accessToken));
  }

  /**
   * Gets initialized api
   *
   * @param token access token
   * @returns initialized api
   */
  public static getReportsApi(accessToken: string) {
    return new ReportsApi(Api.getConfiguration(accessToken));
  }

  /**
   * Gets initialized api
   *
   * @param token access token
   * @returns initialized api
   */
  public static getQueryQuestionCommentsApi(accessToken: string) {
    return new QueryQuestionCommentsApi(Api.getConfiguration(accessToken));
  }

  /**
   * Gets initialized api
   *
   * @param token access token
   * @returns initialized api
   */
  public static getQueryPagesApi(accessToken: string) {
    return new QueryPagesApi(Api.getConfiguration(accessToken));
  }

  /**
   * Gets initialized api
   *
   * @param token access token
   * @returns initialized api
   */
  public static getQueryQuestionAnswersApi(accessToken: string) {
    return new QueryQuestionAnswersApi(Api.getConfiguration(accessToken));
  }

  /**
   * Gets initialized api
   *
   * @param token access token
   * @returns initialized api
   */
  public static getQueryQuestionCommentCategoriesApi(accessToken: string) {
    return new QueryQuestionCommentCategoriesApi(Api.getConfiguration(accessToken));
  }

  /**
   * Gets initialized api
   *
   * @param token access token
   * @returns initialized api
   */
  public static getPanelExpertiseApi(accessToken: string) {
    return new PanelExpertiseApi(Api.getConfiguration(accessToken));
  }

  /**
   * Gets api configuration
   *
   * @param token acess token
   */
  private static getConfiguration(accessToken: string) {
    return new Configuration({
      basePath: basePath,
      apiKey: `Bearer ${accessToken}`
    });
  }

}