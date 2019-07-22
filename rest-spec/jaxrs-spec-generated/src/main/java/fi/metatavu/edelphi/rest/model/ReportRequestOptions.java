package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ReportRequestOptions   {
  private @Valid List<Long> queryPageIds = new ArrayList<>();  private @Valid List<Long> expertiseGroupIds = new ArrayList<>();  private @Valid Boolean show2dAs1d = null;

  /**
   * Include only speficied page ids
   **/
  public ReportRequestOptions queryPageIds(List<Long> queryPageIds) {
    this.queryPageIds = queryPageIds;
    return this;
  }

  
  @ApiModelProperty(value = "Include only speficied page ids")
  @JsonProperty("queryPageIds")
  public List<Long> getQueryPageIds() {
    return queryPageIds;
  }
  public void setQueryPageIds(List<Long> queryPageIds) {
    this.queryPageIds = queryPageIds;
  }

  /**
   * Include only speficied expertise group ids
   **/
  public ReportRequestOptions expertiseGroupIds(List<Long> expertiseGroupIds) {
    this.expertiseGroupIds = expertiseGroupIds;
    return this;
  }

  
  @ApiModelProperty(value = "Include only speficied expertise group ids")
  @JsonProperty("expertiseGroupIds")
  public List<Long> getExpertiseGroupIds() {
    return expertiseGroupIds;
  }
  public void setExpertiseGroupIds(List<Long> expertiseGroupIds) {
    this.expertiseGroupIds = expertiseGroupIds;
  }

  /**
   * Show 2d answers as 1d graphs instead of 2d graphs
   **/
  public ReportRequestOptions show2dAs1d(Boolean show2dAs1d) {
    this.show2dAs1d = show2dAs1d;
    return this;
  }

  
  @ApiModelProperty(value = "Show 2d answers as 1d graphs instead of 2d graphs")
  @JsonProperty("show2dAs1d")
  public Boolean isisShow2dAs1d() {
    return show2dAs1d;
  }
  public void setShow2dAs1d(Boolean show2dAs1d) {
    this.show2dAs1d = show2dAs1d;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReportRequestOptions reportRequestOptions = (ReportRequestOptions) o;
    return Objects.equals(queryPageIds, reportRequestOptions.queryPageIds) &&
        Objects.equals(expertiseGroupIds, reportRequestOptions.expertiseGroupIds) &&
        Objects.equals(show2dAs1d, reportRequestOptions.show2dAs1d);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryPageIds, expertiseGroupIds, show2dAs1d);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportRequestOptions {\n");
    
    sb.append("    queryPageIds: ").append(toIndentedString(queryPageIds)).append("\n");
    sb.append("    expertiseGroupIds: ").append(toIndentedString(expertiseGroupIds)).append("\n");
    sb.append("    show2dAs1d: ").append(toIndentedString(show2dAs1d)).append("\n");
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
