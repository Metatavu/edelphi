package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class QueryQuestionComment   {
  private @Valid Long id = null;  private @Valid Long categoryId = null;  private @Valid Long parentId = null;  private @Valid Boolean hidden = null;  private @Valid Long queryPageId = null;  private @Valid Long queryReplyId = null;  private @Valid String contents = null;  private @Valid UUID creatorId = null;  private @Valid UUID lastModifierId = null;  private @Valid OffsetDateTime created = null;  private @Valid OffsetDateTime lastModified = null;

  /**
   * Comment&#x27;s id
   **/
  public QueryQuestionComment id(Long id) {
    this.id = id;
    return this;
  }

  
  @ApiModelProperty(value = "Comment's id")
  @JsonProperty("id")
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Comment&#x27;s category id
   **/
  public QueryQuestionComment categoryId(Long categoryId) {
    this.categoryId = categoryId;
    return this;
  }

  
  @ApiModelProperty(value = "Comment's category id")
  @JsonProperty("categoryId")
  public Long getCategoryId() {
    return categoryId;
  }
  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  /**
   * Parent comment&#x27;s id
   **/
  public QueryQuestionComment parentId(Long parentId) {
    this.parentId = parentId;
    return this;
  }

  
  @ApiModelProperty(value = "Parent comment's id")
  @JsonProperty("parentId")
  public Long getParentId() {
    return parentId;
  }
  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  /**
   * Whether the comment has been hided by the manager
   **/
  public QueryQuestionComment hidden(Boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  
  @ApiModelProperty(value = "Whether the comment has been hided by the manager")
  @JsonProperty("hidden")
  public Boolean isisHidden() {
    return hidden;
  }
  public void setHidden(Boolean hidden) {
    this.hidden = hidden;
  }

  /**
   * Page&#x27;s id where the comment is
   **/
  public QueryQuestionComment queryPageId(Long queryPageId) {
    this.queryPageId = queryPageId;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Page's id where the comment is")
  @JsonProperty("queryPageId")
  @NotNull
  public Long getQueryPageId() {
    return queryPageId;
  }
  public void setQueryPageId(Long queryPageId) {
    this.queryPageId = queryPageId;
  }

  /**
   * Comment&#x27;s query reply id
   **/
  public QueryQuestionComment queryReplyId(Long queryReplyId) {
    this.queryReplyId = queryReplyId;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Comment's query reply id")
  @JsonProperty("queryReplyId")
  @NotNull
  public Long getQueryReplyId() {
    return queryReplyId;
  }
  public void setQueryReplyId(Long queryReplyId) {
    this.queryReplyId = queryReplyId;
  }

  /**
   * Comment&#x27;s contents
   **/
  public QueryQuestionComment contents(String contents) {
    this.contents = contents;
    return this;
  }

  
  @ApiModelProperty(value = "Comment's contents")
  @JsonProperty("contents")
  public String getContents() {
    return contents;
  }
  public void setContents(String contents) {
    this.contents = contents;
  }

  /**
   * Comment&#x27;s creator id
   **/
  public QueryQuestionComment creatorId(UUID creatorId) {
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
  public QueryQuestionComment lastModifierId(UUID lastModifierId) {
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
  public QueryQuestionComment created(OffsetDateTime created) {
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
  public QueryQuestionComment lastModified(OffsetDateTime lastModified) {
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
    QueryQuestionComment queryQuestionComment = (QueryQuestionComment) o;
    return Objects.equals(id, queryQuestionComment.id) &&
        Objects.equals(categoryId, queryQuestionComment.categoryId) &&
        Objects.equals(parentId, queryQuestionComment.parentId) &&
        Objects.equals(hidden, queryQuestionComment.hidden) &&
        Objects.equals(queryPageId, queryQuestionComment.queryPageId) &&
        Objects.equals(queryReplyId, queryQuestionComment.queryReplyId) &&
        Objects.equals(contents, queryQuestionComment.contents) &&
        Objects.equals(creatorId, queryQuestionComment.creatorId) &&
        Objects.equals(lastModifierId, queryQuestionComment.lastModifierId) &&
        Objects.equals(created, queryQuestionComment.created) &&
        Objects.equals(lastModified, queryQuestionComment.lastModified);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, categoryId, parentId, hidden, queryPageId, queryReplyId, contents, creatorId, lastModifierId, created, lastModified);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryQuestionComment {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    categoryId: ").append(toIndentedString(categoryId)).append("\n");
    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    hidden: ").append(toIndentedString(hidden)).append("\n");
    sb.append("    queryPageId: ").append(toIndentedString(queryPageId)).append("\n");
    sb.append("    queryReplyId: ").append(toIndentedString(queryReplyId)).append("\n");
    sb.append("    contents: ").append(toIndentedString(contents)).append("\n");
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
