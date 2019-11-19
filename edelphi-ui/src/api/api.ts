import { QueryQuestionCommentsApi, Configuration, ConfigurationParameters, QueryQuestionCommentCategoriesApi, QueryPagesApi, PanelsApi, QueryQuestionAnswersApi, PanelExpertiseApi, QueriesApi, UsersApi, ReportsApi } from "../generated/client";

/**
 * Helper class for constructing API instances
 */
export default new class Api {

  private defaultParameters: ConfigurationParameters;

  /**
   * Constructor
   */
  constructor() {
    this.defaultParameters = {
      basePath: process.env.REACT_APP_API_BASEPATH
    }
  }

  /**
   * Returns initialized QueryQuestionCommentsApi
   * 
   * @param accessToken access token
   * @returns initialized QueryQuestionCommentsApi
   */
  public getQueryQuestionCommentsService = (accessToken: string): QueryQuestionCommentsApi => {
    return new QueryQuestionCommentsApi(this.getConfiguration(accessToken));
  }

  /**
   * Returns initialized QueryQuestionCommentCategoriesApi
   * 
   * @param accessToken access token
   * @returns initialized QueryQuestionCommentCategoriesApi
   */
  public getQueryQuestionCommentCategoriesService = (accessToken: string): QueryQuestionCommentCategoriesApi => {
    return new QueryQuestionCommentCategoriesApi(this.getConfiguration(accessToken));
  }

  /**
   * Returns initialized QueryPagesApi
   * 
   * @param accessToken access token
   * @returns initialized QueryPagesApi
   */
  public getQueryPagesService = (accessToken: string): QueryPagesApi => {
    return new QueryPagesApi(this.getConfiguration(accessToken));
  }

  /**
   * Returns initialized PanelsApi
   * 
   * @param accessToken access token
   * @returns initialized PanelsApi
   */
  public getPanelsService = (accessToken: string): PanelsApi => {
    return new PanelsApi(this.getConfiguration(accessToken));
  }

  /**
   * Returns initialized QueryQuestionAnswersApi
   * 
   * @param accessToken access token
   * @returns initialized QueryQuestionAnswersApi
   */
  public getQueryQuestionAnswersService = (accessToken: string): QueryQuestionAnswersApi => {
    return new QueryQuestionAnswersApi(this.getConfiguration(accessToken));
  }

  /**
   * Returns initialized PanelExpertiseApi
   * 
   * @param accessToken access token
   * @returns initialized PanelExpertiseApi
   */
  public getPanelExpertiseService = (accessToken: string): PanelExpertiseApi => {
    return new PanelExpertiseApi(this.getConfiguration(accessToken));
  }

  /**
   * Returns initialized QueriesApi
   * 
   * @param accessToken access token
   * @returns initialized QueriesApi
   */
  public getQueriesService = (accessToken: string): QueriesApi => {
    return new QueriesApi(this.getConfiguration(accessToken));
  }

  /**
   * Returns initialized UsersApi
   * 
   * @param accessToken access token
   * @returns initialized UsersApi
   */
  public getUsersService = (accessToken: string): UsersApi => {
    return new UsersApi(this.getConfiguration(accessToken));
  }

  /**
   * Returns initialized ReportsApi
   * 
   * @param accessToken access token
   * @returns initialized ReportsApi
   */
  public getReportsService = (accessToken: string): ReportsApi => {
    return new ReportsApi(this.getConfiguration(accessToken));
  }

  /**
   * Create API configuration
   * 
   * @param accessToken access token
   * @returns API configuration
   */
  private getConfiguration = (accessToken: string) => {
    return new Configuration({
      apiKey: `Bearer ${accessToken}`,
      ... this.defaultParameters
    });
  }

}