package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets PanelInvitationState
 */
public enum PanelInvitationState {
  IN_QUEUE("IN_QUEUE"),
    BEING_SENT("BEING_SENT"),
    SEND_FAIL("SEND_FAIL"),
    PENDING("PENDING"),
    ADDED("ADDED"),
    ACCEPTED("ACCEPTED"),
    DECLINED("DECLINED");

  private String value;

  PanelInvitationState(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static PanelInvitationState fromValue(String text) {
    for (PanelInvitationState b : PanelInvitationState.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}