package fi.metatavu.edelphi.queries;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;

/**
 * Comparator that compares query option field options by numeric value
 * 
 * @author Antti Leppä
 */
public class QueryOptionFieldOptionComparator implements Comparator<QueryOptionFieldOption> {
  
  @Override
  public int compare(QueryOptionFieldOption option1, QueryOptionFieldOption option2) {
    if (option1.getValue() == option2.getValue()) {
      return 0;
    }
    
    if (!StringUtils.isNumeric(option1.getValue()) || !StringUtils.isNumeric(option2.getValue())) {
      return option1.getId().compareTo(option2.getId());
    }
    
    Long value1 = NumberUtils.createLong(option1.getValue());
    Long value2 = NumberUtils.createLong(option2.getValue());
    
    return value1.compareTo(value2);
  }
  
}