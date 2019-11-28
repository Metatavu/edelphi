package fi.metatavu.edelphi.rest.test.functional.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openapitools.client.model.Panel;
import org.openapitools.client.model.Query;
import org.openapitools.client.model.QueryPageScale1d;
import org.openapitools.client.model.QueryQuestionAnswerScale1d;
import org.openapitools.client.model.QueryReply;
import org.openapitools.client.model.QuerySection;

/**
 * Query page scale 1d functional tests
 * 
 * @author Antti Leppä
 */
public class QueryPageScale1dTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateQueryPageScale1d() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Panel panel = builder.admin().panels().create();
      Query query = builder.admin().queries().create(panel);
      QuerySection querySection = builder.admin().querySections().create(panel, query);
      assertNotNull(builder.admin().queryScale1d().create(panel, query, querySection));
    }
  }
  
  @Test
  public void testQueryPageScale1dAnswer() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Panel panel = builder.admin().panels().create();
      Query query = builder.admin().queries().create(panel);
      QuerySection querySection = builder.admin().querySections().create(panel, query);
      QueryPageScale1d queryPage = builder.admin().queryScale1d().create(panel, query, querySection);
      QueryReply queryReply = builder.admin().queryReplies().create(panel, query);
      
      QueryQuestionAnswerScale1d createdAnswer = builder.admin().scale1dAnswers().upsert(panel, queryPage, queryReply, "3");      
      assertNotNull(createdAnswer);
      assertEquals("3", createdAnswer.getData().getValue());
      
      QueryQuestionAnswerScale1d foundAnswer = builder.admin().scale1dAnswers().findAnswer(panel, queryPage, queryReply);
      assertNotNull(foundAnswer);
      assertEquals("3", foundAnswer.getData().getValue());
      
      QueryQuestionAnswerScale1d updatedAnswer = builder.admin().scale1dAnswers().upsert(panel, queryPage, queryReply, "1");      
      assertNotNull(updatedAnswer);
      assertEquals("1", updatedAnswer.getData().getValue());
      
      foundAnswer = builder.admin().scale1dAnswers().findAnswer(panel, queryPage, queryReply);
      assertNotNull(foundAnswer);
      assertEquals("1", foundAnswer.getData().getValue());
    }
  }

}
