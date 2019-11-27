package fi.metatavu.edelphi.queries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;

/**
 * Controller for query pages
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryFieldController {
  
  public static final String SCALE1D_FIELD_NAME = "scale1d";
  
  private static final int MAX_QUERY_FIELD_CAPTION = 192;

  @Inject
  private QueryFieldDAO queryFieldDAO;

  @Inject
  private QueryOptionFieldOptionDAO queryOptionFieldOptionDAO;

  @Inject
  private QueryOptionFieldDAO queryOptionFieldDAO;

  @Inject
  private QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO;

  /**
   * Finds a query option field by query page and field name
   * 
   * @param queryPage query page
   * @param fieldName field name
   * @return query option field 
   */
  public QueryOptionField findQueryOptionField(QueryPage queryPage, String fieldName) {
    QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    if (queryField instanceof QueryOptionField) {
      return (QueryOptionField) queryField;
    }

    return null;
  }
  
  /**
   * Lists query option field options
   * 
   * @param queryField query field
   * @return field options
   */
  public List<QueryOptionFieldOption> listQueryOptionFieldOptions(QueryOptionField queryField) {
    List<QueryOptionFieldOption> result = queryOptionFieldOptionDAO.listByQueryField(queryField);
    Collections.sort(result, new QueryOptionFieldOptionComparator());
    return result;
  }

  /**
   * Synchronizes scale 1d field
   * 
   * @param queryPage query
   * @param options options
   * @param fieldCaption field caption
   * @param hasAnswers whether the field has answers or not
   */
  public void synchronizeFieldsScale1d(QueryPage queryPage, List<String> options, String fieldCaption, boolean hasAnswers) {
    if (!hasAnswers) {
      Boolean mandatory = false;
      synchronizeOptionsField(queryPage, options, SCALE1D_FIELD_NAME, fieldCaption, mandatory);
    } else {
      synchronizeFieldCaption(queryPage, SCALE1D_FIELD_NAME, fieldCaption);
    }
  }

  /**
   * Deletes query page fields
   * 
   * @param queryPage query page
   */
  public void deleteQueryPageFields(QueryPage queryPage) {
    queryFieldDAO.listByQueryPage(queryPage).stream()
      .forEach(this::deleteQueryField);
  }
  
  /**
   * Deletes query page field
   * 
   * @param queryField query page field
   */
  private void deleteQueryField(QueryField queryField) {
    if (queryField instanceof QueryOptionField) {
      queryOptionFieldOptionDAO.listByQueryField((QueryOptionField) queryField).stream()
        .forEach(queryOptionFieldOptionDAO::delete);
    }
    
    queryFieldDAO.delete(queryField);
  }

  /**
   * Synchronizes field meta. Should not be used when field already contains replies
   * 
   * @param queryPage query page
   * @param options field  options
   * @param fieldName field name
   * @param fieldCaption field caption 
   * @param mandatory whether field is mandatory
   */
  private void synchronizeOptionsField(QueryPage queryPage, List<String> options, String fieldName, String fieldCaption, Boolean mandatory) {
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    if (queryField != null) {
      queryFieldDAO.updateMandatory(queryField, mandatory);
      queryFieldDAO.updateCaption(queryField, StringUtils.abbreviate(fieldCaption, MAX_QUERY_FIELD_CAPTION));
    } else {
      queryField = queryOptionFieldDAO.create(queryPage, fieldName, mandatory, fieldCaption);
    }
    
    System.out.println("queryField " + queryField.getId());

    synchronizeOptionsFieldOptions(options, queryField);
  }
  
  /**
   * Updates field caption if field can be found. Used only when query already contains replies.
   * 
   * @param queryPage query page
   * @param fieldName field name
   * @param fieldCaption field caption
   */
  private void synchronizeFieldCaption(QueryPage queryPage, String fieldName, String fieldCaption) {
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    if (queryField != null)
      queryFieldDAO.updateCaption(queryField, StringUtils.abbreviate(fieldCaption, MAX_QUERY_FIELD_CAPTION));
  }
  
  /**
   * Synchronizes field options
   * 
   * @param options options
   * @param queryField query field
   */
  private void synchronizeOptionsFieldOptions(List<String> options, QueryOptionField queryField) {
    List<String> oldOptionValues = new ArrayList<>();
    List<QueryOptionFieldOption> oldOptions = queryOptionFieldOptionDAO.listByQueryField(queryField);
    for (QueryOptionFieldOption oldOption : oldOptions) {
      oldOptionValues.add(oldOption.getValue());
    }
    
    int i = 0;
    for (String option : options) {
      String optionValue = String.valueOf(i);
      
      QueryOptionFieldOption optionFieldOption = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryField, optionValue);
      if (optionFieldOption == null) {
        queryOptionFieldOptionDAO.create(queryField, option, optionValue);
      } else {
        queryOptionFieldOptionDAO.updateText(optionFieldOption, option);
      }
          
      oldOptionValues.remove(optionValue);
      
      i++;
    }
    
    System.out.println("synchronizeOptionsFieldOptions: 3");
    
    for (String optionValue : oldOptionValues) {
      QueryOptionFieldOption optionFieldOption = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryField, optionValue);
      if (optionFieldOption != null) {
        long answerCount = queryQuestionOptionAnswerDAO.countByQueryOptionFieldOption(optionFieldOption);
        if (answerCount == 0) {
          queryOptionFieldOptionDAO.delete(optionFieldOption);
        } else {
          queryOptionFieldOptionDAO.archive(optionFieldOption);
        }
      }
    }
    
    System.out.println("synchronizeOptionsFieldOptions: 4");
  }
  
}
