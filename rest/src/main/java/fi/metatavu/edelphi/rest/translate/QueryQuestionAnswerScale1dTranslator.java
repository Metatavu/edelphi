package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.queries.QueryQuestionAnswer;
import fi.metatavu.edelphi.queries.QueryQuestionAnswerData;
import fi.metatavu.edelphi.queries.QueryQuestionScale1dAnswerData;

/**
 * Translator for QueryQuestionAnswers
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryQuestionAnswerScale1dTranslator extends AbstractTranslator<QueryQuestionAnswer<?>, fi.metatavu.edelphi.rest.model.QueryQuestionAnswerScale1d> {
  
  @Override
  public fi.metatavu.edelphi.rest.model.QueryQuestionAnswerScale1d translate(QueryQuestionAnswer<?> answerData) {
    QueryQuestionAnswerData data = answerData.getData();
    if (answerData == null || answerData.getQueryPage() == null || answerData.getQueryReply() == null || data == null || !(data instanceof QueryQuestionScale1dAnswerData)) {
      return null;
    }
    
    QueryQuestionScale1dAnswerData scale1dData = (QueryQuestionScale1dAnswerData) data;
    
    QueryPage queryPage = answerData.getQueryPage();
    QueryReply queryReply = answerData.getQueryReply();
    Long queryPageId = queryPage.getId();
    Long queryReplyId = queryReply.getId();
    String id = String.format("%d-%d", queryPageId, queryReplyId);
    
    fi.metatavu.edelphi.rest.model.QueryQuestionAnswerScale1d result = new fi.metatavu.edelphi.rest.model.QueryQuestionAnswerScale1d();
    result.setData(createData(queryPage, scale1dData));
    result.setId(id);
    result.setQueryPageId(queryPageId);
    result.setQueryReplyId(queryReplyId);
    
    return result;
  }

  /**
   * Creates answer data object for scale 1d
   * 
   * @param queryPage query page
   * @param scale1dData data
   * @return answer data object
   */
  private fi.metatavu.edelphi.rest.model.QueryQuestionScale1dAnswerData createData(QueryPage queryPage, QueryQuestionScale1dAnswerData scale1dData) {
    fi.metatavu.edelphi.rest.model.QueryQuestionScale1dAnswerData result = new fi.metatavu.edelphi.rest.model.QueryQuestionScale1dAnswerData();
    result.setValue(scale1dData.getValue());
    return result;
  }

}
