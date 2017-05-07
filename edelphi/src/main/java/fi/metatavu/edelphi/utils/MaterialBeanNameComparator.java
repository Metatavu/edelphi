package fi.metatavu.edelphi.utils;

import java.util.Comparator;

public class MaterialBeanNameComparator implements Comparator<MaterialBean> {
  
  @Override
  public int compare(MaterialBean o1, MaterialBean o2) {
    return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
  }
  
}