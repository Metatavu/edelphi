package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class PanelInvitationRequest   {
  private @Valid List<String> emails = new ArrayList<>();  private @Valid String invitationMessage = null;  private @Valid Long targetQueryId = null;  private @Valid Boolean skipInvitation = null;  private @Valid String password = null;

  /**
   * List of emails the invitation will be sent to
   **/
  public PanelInvitationRequest emails(List<String> emails) {
    this.emails = emails;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "List of emails the invitation will be sent to")
  @JsonProperty("emails")
  @NotNull
  public List<String> getEmails() {
    return emails;
  }
  public void setEmails(List<String> emails) {
    this.emails = emails;
  }

  /**
   * Inviation email content
   **/
  public PanelInvitationRequest invitationMessage(String invitationMessage) {
    this.invitationMessage = invitationMessage;
    return this;
  }

  
  @ApiModelProperty(value = "Inviation email content")
  @JsonProperty("invitationMessage")
  public String getInvitationMessage() {
    return invitationMessage;
  }
  public void setInvitationMessage(String invitationMessage) {
    this.invitationMessage = invitationMessage;
  }

  /**
   * Specify target query for invitation link. If this is left blank, link will lead to panel index page
   **/
  public PanelInvitationRequest targetQueryId(Long targetQueryId) {
    this.targetQueryId = targetQueryId;
    return this;
  }

  
  @ApiModelProperty(value = "Specify target query for invitation link. If this is left blank, link will lead to panel index page")
  @JsonProperty("targetQueryId")
  public Long getTargetQueryId() {
    return targetQueryId;
  }
  public void setTargetQueryId(Long targetQueryId) {
    this.targetQueryId = targetQueryId;
  }

  /**
   * If skip invitation is true, users will be added directly without invitation
   **/
  public PanelInvitationRequest skipInvitation(Boolean skipInvitation) {
    this.skipInvitation = skipInvitation;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "If skip invitation is true, users will be added directly without invitation")
  @JsonProperty("skipInvitation")
  @NotNull
  public Boolean isisSkipInvitation() {
    return skipInvitation;
  }
  public void setSkipInvitation(Boolean skipInvitation) {
    this.skipInvitation = skipInvitation;
  }

  /**
   * Initial password for users. This field is used only when skipInvitation is true
   **/
  public PanelInvitationRequest password(String password) {
    this.password = password;
    return this;
  }

  
  @ApiModelProperty(value = "Initial password for users. This field is used only when skipInvitation is true")
  @JsonProperty("password")
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PanelInvitationRequest panelInvitationRequest = (PanelInvitationRequest) o;
    return Objects.equals(emails, panelInvitationRequest.emails) &&
        Objects.equals(invitationMessage, panelInvitationRequest.invitationMessage) &&
        Objects.equals(targetQueryId, panelInvitationRequest.targetQueryId) &&
        Objects.equals(skipInvitation, panelInvitationRequest.skipInvitation) &&
        Objects.equals(password, panelInvitationRequest.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emails, invitationMessage, targetQueryId, skipInvitation, password);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PanelInvitationRequest {\n");
    
    sb.append("    emails: ").append(toIndentedString(emails)).append("\n");
    sb.append("    invitationMessage: ").append(toIndentedString(invitationMessage)).append("\n");
    sb.append("    targetQueryId: ").append(toIndentedString(targetQueryId)).append("\n");
    sb.append("    skipInvitation: ").append(toIndentedString(skipInvitation)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
