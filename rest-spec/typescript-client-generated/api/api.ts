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

  
  public getQueryQuestionCommentsService(token: string): QueryQuestionCommentsService {
    return new QueryQuestionCommentsService(this.apiUrl, token);
  }
  
}
