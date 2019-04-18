package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class QueryPageLive2DOptions   {
  private @Valid QueryPageLive2DAxis axisX = null;  private @Valid QueryPageLive2DAxis axisY = null;

  /**
   **/
  public QueryPageLive2DOptions axisX(QueryPageLive2DAxis axisX) {
    this.axisX = axisX;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("axisX")
  public QueryPageLive2DAxis getAxisX() {
    return axisX;
  }
  public void setAxisX(QueryPageLive2DAxis axisX) {
    this.axisX = axisX;
  }

  /**
   **/
  public QueryPageLive2DOptions axisY(QueryPageLive2DAxis axisY) {
    this.axisY = axisY;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("axisY")
  public QueryPageLive2DAxis getAxisY() {
    return axisY;
  }
  public void setAxisY(QueryPageLive2DAxis axisY) {
    this.axisY = axisY;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryPageLive2DOptions queryPageLive2DOptions = (QueryPageLive2DOptions) o;
    return Objects.equals(axisX, queryPageLive2DOptions.axisX) &&
        Objects.equals(axisY, queryPageLive2DOptions.axisY);
  }

  @Override
  public int hashCode() {
    return Objects.hash(axisX, axisY);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryPageLive2DOptions {\n");
    
    sb.append("    axisX: ").append(toIndentedString(axisX)).append("\n");
    sb.append("    axisY: ").append(toIndentedString(axisY)).append("\n");
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
