package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class QueryPage   {
  private @Valid Long id = null;  private @Valid Integer pageNumber = null;  private @Valid String title = null;  private @Valid QueryPageType type = null;  private @Valid QueryPageCommentOptions commentOptions = null;  private @Valid Object queryOptions = null;

  /**
   **/
  public QueryPage id(Long id) {
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
  public QueryPage pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("pageNumber")
  @NotNull
  public Integer getPageNumber() {
    return pageNumber;
  }
  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  /**
   **/
  public QueryPage title(String title) {
    this.title = title;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("title")
  @NotNull
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   **/
  public QueryPage type(QueryPageType type) {
    this.type = type;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("type")
  @NotNull
  public QueryPageType getType() {
    return type;
  }
  public void setType(QueryPageType type) {
    this.type = type;
  }

  /**
   **/
  public QueryPage commentOptions(QueryPageCommentOptions commentOptions) {
    this.commentOptions = commentOptions;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("commentOptions")
  @NotNull
  public QueryPageCommentOptions getCommentOptions() {
    return commentOptions;
  }
  public void setCommentOptions(QueryPageCommentOptions commentOptions) {
    this.commentOptions = commentOptions;
  }

  /**
   **/
  public QueryPage queryOptions(Object queryOptions) {
    this.queryOptions = queryOptions;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("queryOptions")
  @NotNull
  public Object getQueryOptions() {
    return queryOptions;
  }
  public void setQueryOptions(Object queryOptions) {
    this.queryOptions = queryOptions;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryPage queryPage = (QueryPage) o;
    return Objects.equals(id, queryPage.id) &&
        Objects.equals(pageNumber, queryPage.pageNumber) &&
        Objects.equals(title, queryPage.title) &&
        Objects.equals(type, queryPage.type) &&
        Objects.equals(commentOptions, queryPage.commentOptions) &&
        Objects.equals(queryOptions, queryPage.queryOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, pageNumber, title, type, commentOptions, queryOptions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryPage {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    commentOptions: ").append(toIndentedString(commentOptions)).append("\n");
    sb.append("    queryOptions: ").append(toIndentedString(queryOptions)).append("\n");
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
