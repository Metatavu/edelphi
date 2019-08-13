package fi.metatavu.edelphi.queries;

import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;

/**
 * Query question answer
 * 
 * @author Antti Leppä
 *
 * @param <D> type of answer data
 */
public class QueryQuestionAnswer <D extends QueryQuestionAnswerData> {

  private QueryReply queryReply;

  private QueryPage queryPage;
  
  private D data;

  /**
   * Constructor
   * 
   * @param queryReply query reply
   * @param queryPage query page
   * @param data answer data
   */
  public QueryQuestionAnswer(QueryReply queryReply, QueryPage queryPage, D data) {
    super();
    this.queryReply = queryReply;
    this.queryPage = queryPage;
    this.data = data;
  }

  /**
   * Returns query reply
   * 
   * @return query reply
   */
  public QueryReply getQueryReply() {
    return queryReply;
  }

  /**
   * Returns query page
   * 
   * @return query page
   */
  public QueryPage getQueryPage() {
    return queryPage;
  }
  
  /**
   * Returns answer data
   * 
   * @return answer data
   */
  public D getData() {
    return data;
  }

}
