package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets QueryPageLive2DAnswersVisibleOption
 */
public enum QueryPageLive2DAnswersVisibleOption {
  IMMEDIATELY("IMMEDIATELY"),
    AFTER_OWN_ANSWER("AFTER_OWN_ANSWER");

  private String value;

  QueryPageLive2DAnswersVisibleOption(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static QueryPageLive2DAnswersVisibleOption fromValue(String text) {
    for (QueryPageLive2DAnswersVisibleOption b : QueryPageLive2DAnswersVisibleOption.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}