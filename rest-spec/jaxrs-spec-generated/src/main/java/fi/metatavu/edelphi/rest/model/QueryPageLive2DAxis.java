package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class QueryPageLive2DAxis   {
  private @Valid String label = null;  private @Valid String color = null;

  /**
   **/
  public QueryPageLive2DAxis label(String label) {
    this.label = label;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("label")
  public String getLabel() {
    return label;
  }
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   **/
  public QueryPageLive2DAxis color(String color) {
    this.color = color;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("color")
  public String getColor() {
    return color;
  }
  public void setColor(String color) {
    this.color = color;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryPageLive2DAxis queryPageLive2DAxis = (QueryPageLive2DAxis) o;
    return Objects.equals(label, queryPageLive2DAxis.label) &&
        Objects.equals(color, queryPageLive2DAxis.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, color);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryPageLive2DAxis {\n");
    
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    color: ").append(toIndentedString(color)).append("\n");
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
