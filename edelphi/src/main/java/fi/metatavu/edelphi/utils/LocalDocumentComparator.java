package fi.metatavu.edelphi.utils;

import java.util.Comparator;

import fi.metatavu.edelphi.domainmodel.resources.LocalDocumentPage;

public class LocalDocumentComparator implements Comparator<LocalDocumentPage> {
  @Override
  public int compare(LocalDocumentPage o1, LocalDocumentPage o2) {
    return o1.getPageNumber() - o2.getPageNumber();
  }
}