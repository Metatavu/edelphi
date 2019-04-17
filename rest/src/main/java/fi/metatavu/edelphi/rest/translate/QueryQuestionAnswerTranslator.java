package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.queries.QueryQuestionAnswer;
import fi.metatavu.edelphi.queries.QueryQuestionAnswerData;
import fi.metatavu.edelphi.queries.QueryQuestionLive2dAnswerData;
import fi.metatavu.edelphi.rest.model.QueryQuestionAnswerDataLive2d;

/**
 * Translator for QueryQuestionAnswers
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryQuestionAnswerTranslator extends AbstractTranslator<QueryQuestionAnswer<?>, fi.metatavu.edelphi.rest.model.QueryQuestionAnswer> {
  
  @Override
  public fi.metatavu.edelphi.rest.model.QueryQuestionAnswer translate(QueryQuestionAnswer<?> answerData) {
    QueryQuestionAnswerData data = answerData.getData();
    if (answerData == null || answerData.getQueryPage() == null || answerData.getQueryReply() == null || data == null) {
      return null;
    }
    
    QueryPage queryPage = answerData.getQueryPage();
    QueryReply queryReply = answerData.getQueryReply();
    Long queryPageId = queryPage.getId();
    Long queryReplyId = queryReply.getId();
    String id = String.format("%d-%d", queryPageId, queryReplyId);
    
    fi.metatavu.edelphi.rest.model.QueryQuestionAnswer result = new fi.metatavu.edelphi.rest.model.QueryQuestionAnswer();
    result.setData(createData(queryPage, data));
    result.setId(id);
    result.setQueryPageId(queryPageId);
    result.setQueryReplyId(queryReplyId);
    
    return result;
  }

  /**
   * Creates answer data object
   * 
   * @param queryPage query page
   * @param data data
   * @return answer data object
   */
  private Object createData(QueryPage queryPage, QueryQuestionAnswerData data) {
    switch (queryPage.getPageType()) {
      case LIVE_2D:
        return createLive2dData(queryPage, (QueryQuestionLive2dAnswerData) data);
      default:
      break;
    }
    
    return null;
  }

  /**
   * Creates answer data object for live 2d
   * 
   * @param queryPage query page
   * @param data data
   * @return answer data object
   */
  private QueryQuestionAnswerDataLive2d createLive2dData(QueryPage queryPage, QueryQuestionLive2dAnswerData data) {
    QueryQuestionAnswerDataLive2d result = new QueryQuestionAnswerDataLive2d();
    result.setX(data.getX());
    result.setY(data.getY());
    return result;
  }

}
