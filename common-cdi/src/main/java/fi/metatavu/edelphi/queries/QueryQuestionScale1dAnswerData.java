package fi.metatavu.edelphi.queries;

/**
 * Answer data implementation for scale 1d queries
 * 
 * @author Antti Leppä
 */
public class QueryQuestionScale1dAnswerData implements QueryQuestionAnswerData {

  private String value;

  /**
   * Constructor 
   * 
   * @param value value
   */
  public QueryQuestionScale1dAnswerData(String value) {
    super();
    this.value = value;
  }

  /**
   * Returns value
   * 
   * @return value
   */
  public String getValue() {
    return value;
  }
  
  /**
   * Sets value
   * 
   * @param value value
   */
  public void setValue(String value) {
    this.value = value;
  }

}
