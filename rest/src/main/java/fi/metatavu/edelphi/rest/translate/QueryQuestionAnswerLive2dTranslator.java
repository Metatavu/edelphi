package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.queries.QueryQuestionAnswer;
import fi.metatavu.edelphi.queries.QueryQuestionAnswerData;
import fi.metatavu.edelphi.queries.QueryQuestionLive2dAnswerData;

/**
 * Translator for QueryQuestionAnswers
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryQuestionAnswerLive2dTranslator extends AbstractTranslator<QueryQuestionAnswer<?>, fi.metatavu.edelphi.rest.model.QueryQuestionAnswerLive2d> {
  
  @Override
  public fi.metatavu.edelphi.rest.model.QueryQuestionAnswerLive2d translate(QueryQuestionAnswer<?> answerData) {
    QueryQuestionAnswerData data = answerData.getData();
    if (answerData == null || answerData.getQueryPage() == null || answerData.getQueryReply() == null || data == null || !(data instanceof QueryQuestionLive2dAnswerData)) {
      return null;
    }
    
    QueryQuestionLive2dAnswerData live2dData = (QueryQuestionLive2dAnswerData) data;
    
    QueryPage queryPage = answerData.getQueryPage();
    QueryReply queryReply = answerData.getQueryReply();
    Long queryPageId = queryPage.getId();
    Long queryReplyId = queryReply.getId();
    String id = String.format("%d-%d", queryPageId, queryReplyId);
    
    fi.metatavu.edelphi.rest.model.QueryQuestionAnswerLive2d result = new fi.metatavu.edelphi.rest.model.QueryQuestionAnswerLive2d();
    result.setData(createData(queryPage, live2dData));
    result.setId(id);
    result.setQueryPageId(queryPageId);
    result.setQueryReplyId(queryReplyId);
    
    return result;
  }

  /**
   * Creates answer data object for live 2d
   * 
   * @param queryPage query page
   * @param live2dData data
   * @return answer data object
   */
  private fi.metatavu.edelphi.rest.model.QueryQuestionLive2dAnswerData createData(QueryPage queryPage, QueryQuestionLive2dAnswerData live2dData) {
    fi.metatavu.edelphi.rest.model.QueryQuestionLive2dAnswerData result = new fi.metatavu.edelphi.rest.model.QueryQuestionLive2dAnswerData();
    result.setX(live2dData.getX());
    result.setY(live2dData.getY());
    return result;
  }

}
