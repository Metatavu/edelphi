package fi.metatavu.edelphi.utils;

import java.util.Comparator;

import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;

public class QueryPageNumberComparator implements Comparator<QueryPage> {
  @Override
  public int compare(QueryPage o1, QueryPage o2) {
    return o1.getPageNumber() - o2.getPageNumber();
  }
}