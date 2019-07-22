export * from './panelExpertise.service';
import { PanelExpertiseService } from './panelExpertise.service';
export * from './panels.service';
import { PanelsService } from './panels.service';
export * from './queries.service';
import { QueriesService } from './queries.service';
export * from './queryPages.service';
import { QueryPagesService } from './queryPages.service';
export * from './queryQuestionAnswers.service';
import { QueryQuestionAnswersService } from './queryQuestionAnswers.service';
export * from './queryQuestionCommentCategories.service';
import { QueryQuestionCommentCategoriesService } from './queryQuestionCommentCategories.service';
export * from './queryQuestionComments.service';
import { QueryQuestionCommentsService } from './queryQuestionComments.service';
export * from './reports.service';
import { ReportsService } from './reports.service';

export class ApiUtils {
  /**
   * Handles response from API
   * 
   * @param response response object
   */
  public static handleResponse(response: any) {
    switch (response.status) {
      case 202:
      case 204:
        return {};
      default:
        return response.json();
    }
  }
}

export default new class Api {

  private apiUrl = "http://localhost";

  /**
   * Configures api endpoint
   *
   */
  public configure(baseUrl: string) {
    this.apiUrl = baseUrl;
  }

  
  public getPanelExpertiseService(token: string): PanelExpertiseService {
    return new PanelExpertiseService(this.apiUrl, token);
  }
  
  public getPanelsService(token: string): PanelsService {
    return new PanelsService(this.apiUrl, token);
  }
  
  public getQueriesService(token: string): QueriesService {
    return new QueriesService(this.apiUrl, token);
  }
  
  public getQueryPagesService(token: string): QueryPagesService {
    return new QueryPagesService(this.apiUrl, token);
  }
  
  public getQueryQuestionAnswersService(token: string): QueryQuestionAnswersService {
    return new QueryQuestionAnswersService(this.apiUrl, token);
  }
  
  public getQueryQuestionCommentCategoriesService(token: string): QueryQuestionCommentCategoriesService {
    return new QueryQuestionCommentCategoriesService(this.apiUrl, token);
  }
  
  public getQueryQuestionCommentsService(token: string): QueryQuestionCommentsService {
    return new QueryQuestionCommentsService(this.apiUrl, token);
  }
  
  public getReportsService(token: string): ReportsService {
    return new ReportsService(this.apiUrl, token);
  }
  
}
