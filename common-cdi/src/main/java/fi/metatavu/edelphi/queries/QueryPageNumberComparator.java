package fi.metatavu.edelphi.queries;

import java.util.Comparator;

import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;

/**
 * Comparator that compares query pages by their page number
 * 
 * @author Antti Leppä
 */
public class QueryPageNumberComparator implements Comparator<QueryPage> {
  
  @Override
  public int compare(QueryPage o1, QueryPage o2) {
    return o1.getPageNumber() - o2.getPageNumber();
  }
  
}