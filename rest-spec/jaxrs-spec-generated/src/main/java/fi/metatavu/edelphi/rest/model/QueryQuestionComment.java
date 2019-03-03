package fi.metatavu.edelphi.rest.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class QueryQuestionComment   {
  private @Valid UUID id = null;  private @Valid UUID parentId = null;  private @Valid Boolean hidden = null;  private @Valid UUID queryPageId = null;  private @Valid UUID queryReplyId = null;  private @Valid String comment = null;  private @Valid UUID creatorId = null;  private @Valid UUID lastModifierId = null;  private @Valid OffsetDateTime created = null;  private @Valid OffsetDateTime lastModified = null;

  /**
   **/
  public QueryQuestionComment id(UUID id) {
    this.id = id;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   **/
  public QueryQuestionComment parentId(UUID parentId) {
    this.parentId = parentId;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("parentId")
  public UUID getParentId() {
    return parentId;
  }
  public void setParentId(UUID parentId) {
    this.parentId = parentId;
  }

  /**
   **/
  public QueryQuestionComment hidden(Boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("hidden")
  public Boolean isisHidden() {
    return hidden;
  }
  public void setHidden(Boolean hidden) {
    this.hidden = hidden;
  }

  /**
   **/
  public QueryQuestionComment queryPageId(UUID queryPageId) {
    this.queryPageId = queryPageId;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("queryPageId")
  @NotNull
  public UUID getQueryPageId() {
    return queryPageId;
  }
  public void setQueryPageId(UUID queryPageId) {
    this.queryPageId = queryPageId;
  }

  /**
   **/
  public QueryQuestionComment queryReplyId(UUID queryReplyId) {
    this.queryReplyId = queryReplyId;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("queryReplyId")
  @NotNull
  public UUID getQueryReplyId() {
    return queryReplyId;
  }
  public void setQueryReplyId(UUID queryReplyId) {
    this.queryReplyId = queryReplyId;
  }

  /**
   **/
  public QueryQuestionComment comment(String comment) {
    this.comment = comment;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("comment")
  @NotNull
  public String getComment() {
    return comment;
  }
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   **/
  public QueryQuestionComment creatorId(UUID creatorId) {
    this.creatorId = creatorId;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("creatorId")
  public UUID getCreatorId() {
    return creatorId;
  }
  public void setCreatorId(UUID creatorId) {
    this.creatorId = creatorId;
  }

  /**
   **/
  public QueryQuestionComment lastModifierId(UUID lastModifierId) {
    this.lastModifierId = lastModifierId;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("lastModifierId")
  public UUID getLastModifierId() {
    return lastModifierId;
  }
  public void setLastModifierId(UUID lastModifierId) {
    this.lastModifierId = lastModifierId;
  }

  /**
   **/
  public QueryQuestionComment created(OffsetDateTime created) {
    this.created = created;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("created")
  public OffsetDateTime getCreated() {
    return created;
  }
  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }

  /**
   **/
  public QueryQuestionComment lastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
    return this;
  }

  
  @ApiModelProperty(value = "")
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
    QueryQuestionComment queryQuestionComment = (QueryQuestionComment) o;
    return Objects.equals(id, queryQuestionComment.id) &&
        Objects.equals(parentId, queryQuestionComment.parentId) &&
        Objects.equals(hidden, queryQuestionComment.hidden) &&
        Objects.equals(queryPageId, queryQuestionComment.queryPageId) &&
        Objects.equals(queryReplyId, queryQuestionComment.queryReplyId) &&
        Objects.equals(comment, queryQuestionComment.comment) &&
        Objects.equals(creatorId, queryQuestionComment.creatorId) &&
        Objects.equals(lastModifierId, queryQuestionComment.lastModifierId) &&
        Objects.equals(created, queryQuestionComment.created) &&
        Objects.equals(lastModified, queryQuestionComment.lastModified);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, parentId, hidden, queryPageId, queryReplyId, comment, creatorId, lastModifierId, created, lastModified);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryQuestionComment {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    hidden: ").append(toIndentedString(hidden)).append("\n");
    sb.append("    queryPageId: ").append(toIndentedString(queryPageId)).append("\n");
    sb.append("    queryReplyId: ").append(toIndentedString(queryReplyId)).append("\n");
    sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
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
