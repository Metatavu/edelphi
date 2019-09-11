package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class QueryQuestionCommentCategory   {
  private @Valid Long id = null;  private @Valid String name = null;  private @Valid Long queryId = null;  private @Valid Long queryPageId = null;

  /**
   * Comment category&#x27;s id
   **/
  public QueryQuestionCommentCategory id(Long id) {
    this.id = id;
    return this;
  }

  
  @ApiModelProperty(value = "Comment category's id")
  @JsonProperty("id")
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Comment category&#x27;s name
   **/
  public QueryQuestionCommentCategory name(String name) {
    this.name = name;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Comment category's name")
  @JsonProperty("name")
  @NotNull
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Query&#x27;s id where the comment is
   **/
  public QueryQuestionCommentCategory queryId(Long queryId) {
    this.queryId = queryId;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "Query's id where the comment is")
  @JsonProperty("queryId")
  @NotNull
  public Long getQueryId() {
    return queryId;
  }
  public void setQueryId(Long queryId) {
    this.queryId = queryId;
  }

  /**
   * Query page&#x27;s id or null if category is query scoped
   **/
  public QueryQuestionCommentCategory queryPageId(Long queryPageId) {
    this.queryPageId = queryPageId;
    return this;
  }

  
  @ApiModelProperty(value = "Query page's id or null if category is query scoped")
  @JsonProperty("queryPageId")
  public Long getQueryPageId() {
    return queryPageId;
  }
  public void setQueryPageId(Long queryPageId) {
    this.queryPageId = queryPageId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryQuestionCommentCategory queryQuestionCommentCategory = (QueryQuestionCommentCategory) o;
    return Objects.equals(id, queryQuestionCommentCategory.id) &&
        Objects.equals(name, queryQuestionCommentCategory.name) &&
        Objects.equals(queryId, queryQuestionCommentCategory.queryId) &&
        Objects.equals(queryPageId, queryQuestionCommentCategory.queryPageId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, queryId, queryPageId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryQuestionCommentCategory {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    queryId: ").append(toIndentedString(queryId)).append("\n");
    sb.append("    queryPageId: ").append(toIndentedString(queryPageId)).append("\n");
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
