package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Query   {
  private @Valid Long id = null;  private @Valid Boolean allowEditReply = null;  private @Valid OffsetDateTime closes = null;  private @Valid QueryState state = null;  private @Valid String name = null;  private @Valid String urlName = null;  private @Valid Boolean visible = null;  private @Valid String description = null;  private @Valid UUID creatorId = null;  private @Valid UUID lastModifierId = null;  private @Valid OffsetDateTime created = null;  private @Valid OffsetDateTime lastModified = null;

  /**
   **/
  public Query id(Long id) {
    this.id = id;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("id")
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  /**
   **/
  public Query allowEditReply(Boolean allowEditReply) {
    this.allowEditReply = allowEditReply;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("allowEditReply")
  public Boolean isisAllowEditReply() {
    return allowEditReply;
  }
  public void setAllowEditReply(Boolean allowEditReply) {
    this.allowEditReply = allowEditReply;
  }

  /**
   **/
  public Query closes(OffsetDateTime closes) {
    this.closes = closes;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("closes")
  public OffsetDateTime getCloses() {
    return closes;
  }
  public void setCloses(OffsetDateTime closes) {
    this.closes = closes;
  }

  /**
   **/
  public Query state(QueryState state) {
    this.state = state;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("state")
  public QueryState getState() {
    return state;
  }
  public void setState(QueryState state) {
    this.state = state;
  }

  /**
   **/
  public Query name(String name) {
    this.name = name;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   **/
  public Query urlName(String urlName) {
    this.urlName = urlName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("urlName")
  public String getUrlName() {
    return urlName;
  }
  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  /**
   **/
  public Query visible(Boolean visible) {
    this.visible = visible;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("visible")
  public Boolean isisVisible() {
    return visible;
  }
  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  /**
   **/
  public Query description(String description) {
    this.description = description;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Comment&#x27;s creator id
   **/
  public Query creatorId(UUID creatorId) {
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
  public Query lastModifierId(UUID lastModifierId) {
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
  public Query created(OffsetDateTime created) {
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
  public Query lastModified(OffsetDateTime lastModified) {
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
    Query query = (Query) o;
    return Objects.equals(id, query.id) &&
        Objects.equals(allowEditReply, query.allowEditReply) &&
        Objects.equals(closes, query.closes) &&
        Objects.equals(state, query.state) &&
        Objects.equals(name, query.name) &&
        Objects.equals(urlName, query.urlName) &&
        Objects.equals(visible, query.visible) &&
        Objects.equals(description, query.description) &&
        Objects.equals(creatorId, query.creatorId) &&
        Objects.equals(lastModifierId, query.lastModifierId) &&
        Objects.equals(created, query.created) &&
        Objects.equals(lastModified, query.lastModified);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, allowEditReply, closes, state, name, urlName, visible, description, creatorId, lastModifierId, created, lastModified);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Query {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    allowEditReply: ").append(toIndentedString(allowEditReply)).append("\n");
    sb.append("    closes: ").append(toIndentedString(closes)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    urlName: ").append(toIndentedString(urlName)).append("\n");
    sb.append("    visible: ").append(toIndentedString(visible)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
