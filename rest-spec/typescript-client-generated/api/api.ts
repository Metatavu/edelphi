export * from './queryPages.service';
import { QueryPagesService } from './queryPages.service';
export * from './queryQuestionAnswers.service';
import { QueryQuestionAnswersService } from './queryQuestionAnswers.service';
export * from './queryQuestionComments.service';
import { QueryQuestionCommentsService } from './queryQuestionComments.service';

export class ApiUtils {
  /**
   * Handles response from API
   * 
   * @param response response object
   */
  public static handleResponse(response: any) {
    switch (response.status) {
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

  
  public getQueryPagesService(token: string): QueryPagesService {
    return new QueryPagesService(this.apiUrl, token);
  }
  
  public getQueryQuestionAnswersService(token: string): QueryQuestionAnswersService {
    return new QueryQuestionAnswersService(this.apiUrl, token);
  }
  
  public getQueryQuestionCommentsService(token: string): QueryQuestionCommentsService {
    return new QueryQuestionCommentsService(this.apiUrl, token);
  }
  
}
