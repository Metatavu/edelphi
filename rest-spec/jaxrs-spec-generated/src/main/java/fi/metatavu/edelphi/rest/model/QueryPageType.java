package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets QueryPageType
 */
public enum QueryPageType {
  TEXT("TEXT"),
    FORM("FORM"),
    EXPERTISE("EXPERTISE"),
    THESIS_SCALE_1D("THESIS_SCALE_1D"),
    THESIS_SCALE_2D("THESIS_SCALE_2D"),
    THESIS_ORDER("THESIS_ORDER"),
    THESIS_TIME_SERIE("THESIS_TIME_SERIE"),
    THESIS_MULTI_SELECT("THESIS_MULTI_SELECT"),
    THESIS_TIMELINE("THESIS_TIMELINE"),
    THESIS_GROUPING("THESIS_GROUPING"),
    THESIS_MULTIPLE_2D_SCALES("THESIS_MULTIPLE_2D_SCALES"),
    LIVE_2D("LIVE_2D"),
    COLLAGE_2D("COLLAGE_2D");

  private String value;

  QueryPageType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static QueryPageType fromValue(String text) {
    for (QueryPageType b : QueryPageType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}