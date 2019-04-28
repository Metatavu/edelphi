package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets QueryPageLive2DColor
 */
public enum QueryPageLive2DColor {
  RED("RED"),
    GREEN("GREEN"),
    BLUE("BLUE");

  private String value;

  QueryPageLive2DColor(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static QueryPageLive2DColor fromValue(String text) {
    for (QueryPageLive2DColor b : QueryPageLive2DColor.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}