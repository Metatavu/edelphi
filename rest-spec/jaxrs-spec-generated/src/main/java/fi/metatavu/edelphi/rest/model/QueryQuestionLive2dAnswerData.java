package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class QueryQuestionLive2dAnswerData   {
  private @Valid Double x = null;  private @Valid Double y = null;

  /**
   **/
  public QueryQuestionLive2dAnswerData x(Double x) {
    this.x = x;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("x")
  @NotNull
  public Double getX() {
    return x;
  }
  public void setX(Double x) {
    this.x = x;
  }

  /**
   **/
  public QueryQuestionLive2dAnswerData y(Double y) {
    this.y = y;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("y")
  @NotNull
  public Double getY() {
    return y;
  }
  public void setY(Double y) {
    this.y = y;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryQuestionLive2dAnswerData queryQuestionLive2dAnswerData = (QueryQuestionLive2dAnswerData) o;
    return Objects.equals(x, queryQuestionLive2dAnswerData.x) &&
        Objects.equals(y, queryQuestionLive2dAnswerData.y);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryQuestionLive2dAnswerData {\n");
    
    sb.append("    x: ").append(toIndentedString(x)).append("\n");
    sb.append("    y: ").append(toIndentedString(y)).append("\n");
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
