package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class QueryPageCommentOptions   {
  private @Valid Boolean commentable = null;  private @Valid Boolean discussionVisible = null;  private @Valid List<String> categories = new ArrayList<>();

  /**
   **/
  public QueryPageCommentOptions commentable(Boolean commentable) {
    this.commentable = commentable;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("commentable")
  @NotNull
  public Boolean isisCommentable() {
    return commentable;
  }
  public void setCommentable(Boolean commentable) {
    this.commentable = commentable;
  }

  /**
   **/
  public QueryPageCommentOptions discussionVisible(Boolean discussionVisible) {
    this.discussionVisible = discussionVisible;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("discussionVisible")
  @NotNull
  public Boolean isisDiscussionVisible() {
    return discussionVisible;
  }
  public void setDiscussionVisible(Boolean discussionVisible) {
    this.discussionVisible = discussionVisible;
  }

  /**
   **/
  public QueryPageCommentOptions categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("categories")
  public List<String> getCategories() {
    return categories;
  }
  public void setCategories(List<String> categories) {
    this.categories = categories;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryPageCommentOptions queryPageCommentOptions = (QueryPageCommentOptions) o;
    return Objects.equals(commentable, queryPageCommentOptions.commentable) &&
        Objects.equals(discussionVisible, queryPageCommentOptions.discussionVisible) &&
        Objects.equals(categories, queryPageCommentOptions.categories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commentable, discussionVisible, categories);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryPageCommentOptions {\n");
    
    sb.append("    commentable: ").append(toIndentedString(commentable)).append("\n");
    sb.append("    discussionVisible: ").append(toIndentedString(discussionVisible)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
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
