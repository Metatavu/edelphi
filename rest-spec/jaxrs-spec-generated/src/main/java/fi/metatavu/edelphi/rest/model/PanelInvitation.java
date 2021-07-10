package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class PanelInvitation   {
  private @Valid Long id = null;  private @Valid Long panelId = null;  private @Valid Long queryId = null;  private @Valid String email = null;  private @Valid PanelInvitationState state = null;  private @Valid UUID creatorId = null;  private @Valid UUID lastModifierId = null;  private @Valid OffsetDateTime created = null;  private @Valid OffsetDateTime lastModified = null;

  /**
   * Invitation id
   **/
  public PanelInvitation id(Long id) {
    this.id = id;
    return this;
  }

  
  @ApiModelProperty(value = "Invitation id")
  @JsonProperty("id")
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Panel id
   **/
  public PanelInvitation panelId(Long panelId) {
    this.panelId = panelId;
    return this;
  }

  
  @ApiModelProperty(value = "Panel id")
  @JsonProperty("panelId")
  public Long getPanelId() {
    return panelId;
  }
  public void setPanelId(Long panelId) {
    this.panelId = panelId;
  }

  /**
   * Invitation&#x27;s target query id
   **/
  public PanelInvitation queryId(Long queryId) {
    this.queryId = queryId;
    return this;
  }

  
  @ApiModelProperty(value = "Invitation's target query id")
  @JsonProperty("queryId")
  public Long getQueryId() {
    return queryId;
  }
  public void setQueryId(Long queryId) {
    this.queryId = queryId;
  }

  /**
   * Invitation email
   **/
  public PanelInvitation email(String email) {
    this.email = email;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Invitation email")
  @JsonProperty("email")
  @NotNull
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   **/
  public PanelInvitation state(PanelInvitationState state) {
    this.state = state;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("state")
  @NotNull
  public PanelInvitationState getState() {
    return state;
  }
  public void setState(PanelInvitationState state) {
    this.state = state;
  }

  /**
   * Comment&#x27;s creator id
   **/
  public PanelInvitation creatorId(UUID creatorId) {
    this.creatorId = creatorId;
    return this;
  }

  
  @ApiModelProperty(value = "Comment's creator id")
  @JsonProperty("creatorId")
  public UUID getCreatorId() {
    return creatorId;
  }
  public void setCreatorId(UUID creatorId) {
    this.creatorId = creatorId;
  }

  /**
   * Comment&#x27;s last modifier id
   **/
  public PanelInvitation lastModifierId(UUID lastModifierId) {
    this.lastModifierId = lastModifierId;
    return this;
  }

  
  @ApiModelProperty(value = "Comment's last modifier id")
  @JsonProperty("lastModifierId")
  public UUID getLastModifierId() {
    return lastModifierId;
  }
  public void setLastModifierId(UUID lastModifierId) {
    this.lastModifierId = lastModifierId;
  }

  /**
   * Comment&#x27;s creation time
   **/
  public PanelInvitation created(OffsetDateTime created) {
    this.created = created;
    return this;
  }

  
  @ApiModelProperty(value = "Comment's creation time")
  @JsonProperty("created")
  public OffsetDateTime getCreated() {
    return created;
  }
  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }

  /**
   * Comment&#x27;s last modification time
   **/
  public PanelInvitation lastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
    return this;
  }

  
  @ApiModelProperty(value = "Comment's last modification time")
  @JsonProperty("lastModified")
  public OffsetDateTime getLastModified() {
    return lastModified;
  }
  public void setLastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PanelInvitation panelInvitation = (PanelInvitation) o;
    return Objects.equals(id, panelInvitation.id) &&
        Objects.equals(panelId, panelInvitation.panelId) &&
        Objects.equals(queryId, panelInvitation.queryId) &&
        Objects.equals(email, panelInvitation.email) &&
        Objects.equals(state, panelInvitation.state) &&
        Objects.equals(creatorId, panelInvitation.creatorId) &&
        Objects.equals(lastModifierId, panelInvitation.lastModifierId) &&
        Objects.equals(created, panelInvitation.created) &&
        Objects.equals(lastModified, panelInvitation.lastModified);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, panelId, queryId, email, state, creatorId, lastModifierId, created, lastModified);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PanelInvitation {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    panelId: ").append(toIndentedString(panelId)).append("\n");
    sb.append("    queryId: ").append(toIndentedString(queryId)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    creatorId: ").append(toIndentedString(creatorId)).append("\n");
    sb.append("    lastModifierId: ").append(toIndentedString(lastModifierId)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    lastModified: ").append(toIndentedString(lastModified)).append("\n");
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
