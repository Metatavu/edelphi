package fi.metatavu.edelphi.rest.model;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class QueryQuestionAnswer   {
  private @Valid String id = null;  private @Valid Long queryPageId = null;  private @Valid Long queryReplyId = null;  private @Valid Object data = null;

  /**
   * Id of the query answer. Id is a composed from queryReplyId and queryReplyId by joining them with minus sign (e.g. 123-456)
   **/
  public QueryQuestionAnswer id(String id) {
    this.id = id;
    return this;
  }

  
  @ApiModelProperty(value = "Id of the query answer. Id is a composed from queryReplyId and queryReplyId by joining them with minus sign (e.g. 123-456)")
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   **/
  public QueryQuestionAnswer queryPageId(Long queryPageId) {
    this.queryPageId = queryPageId;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("queryPageId")
  @NotNull
  public Long getQueryPageId() {
    return queryPageId;
  }
  public void setQueryPageId(Long queryPageId) {
    this.queryPageId = queryPageId;
  }

  /**
   **/
  public QueryQuestionAnswer queryReplyId(Long queryReplyId) {
    this.queryReplyId = queryReplyId;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("queryReplyId")
  @NotNull
  public Long getQueryReplyId() {
    return queryReplyId;
  }
  public void setQueryReplyId(Long queryReplyId) {
    this.queryReplyId = queryReplyId;
  }

  /**
   **/
  public QueryQuestionAnswer data(Object data) {
    this.data = data;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("data")
  @NotNull
  public Object getData() {
    return data;
  }
  public void setData(Object data) {
    this.data = data;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryQuestionAnswer queryQuestionAnswer = (QueryQuestionAnswer) o;
    return Objects.equals(id, queryQuestionAnswer.id) &&
        Objects.equals(queryPageId, queryQuestionAnswer.queryPageId) &&
        Objects.equals(queryReplyId, queryQuestionAnswer.queryReplyId) &&
        Objects.equals(data, queryQuestionAnswer.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, queryPageId, queryReplyId, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryQuestionAnswer {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    queryPageId: ").append(toIndentedString(queryPageId)).append("\n");
    sb.append("    queryReplyId: ").append(toIndentedString(queryReplyId)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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
