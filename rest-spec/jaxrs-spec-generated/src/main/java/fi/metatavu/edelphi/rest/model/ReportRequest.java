package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ReportRequest   {
  private @Valid Long panelId = null;  private @Valid Long queryId = null;  private @Valid Long stampId = null;  private @Valid ReportType type = null;  private @Valid ReportFormat format = null;  private @Valid ReportDelivery delivery = null;  private @Valid ReportRequestOptions options = null;

  /**
   **/
  public ReportRequest panelId(Long panelId) {
    this.panelId = panelId;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("panelId")
  @NotNull
  public Long getPanelId() {
    return panelId;
  }
  public void setPanelId(Long panelId) {
    this.panelId = panelId;
  }

  /**
   **/
  public ReportRequest queryId(Long queryId) {
    this.queryId = queryId;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("queryId")
  @NotNull
  public Long getQueryId() {
    return queryId;
  }
  public void setQueryId(Long queryId) {
    this.queryId = queryId;
  }

  /**
   * Panel stamp id. Defaults to current stamp
   **/
  public ReportRequest stampId(Long stampId) {
    this.stampId = stampId;
    return this;
  }

  
  @ApiModelProperty(value = "Panel stamp id. Defaults to current stamp")
  @JsonProperty("stampId")
  public Long getStampId() {
    return stampId;
  }
  public void setStampId(Long stampId) {
    this.stampId = stampId;
  }

  /**
   **/
  public ReportRequest type(ReportType type) {
    this.type = type;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("type")
  @NotNull
  public ReportType getType() {
    return type;
  }
  public void setType(ReportType type) {
    this.type = type;
  }

  /**
   **/
  public ReportRequest format(ReportFormat format) {
    this.format = format;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("format")
  @NotNull
  public ReportFormat getFormat() {
    return format;
  }
  public void setFormat(ReportFormat format) {
    this.format = format;
  }

  /**
   **/
  public ReportRequest delivery(ReportDelivery delivery) {
    this.delivery = delivery;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("delivery")
  public ReportDelivery getDelivery() {
    return delivery;
  }
  public void setDelivery(ReportDelivery delivery) {
    this.delivery = delivery;
  }

  /**
   **/
  public ReportRequest options(ReportRequestOptions options) {
    this.options = options;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("options")
  @NotNull
  public ReportRequestOptions getOptions() {
    return options;
  }
  public void setOptions(ReportRequestOptions options) {
    this.options = options;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReportRequest reportRequest = (ReportRequest) o;
    return Objects.equals(panelId, reportRequest.panelId) &&
        Objects.equals(queryId, reportRequest.queryId) &&
        Objects.equals(stampId, reportRequest.stampId) &&
        Objects.equals(type, reportRequest.type) &&
        Objects.equals(format, reportRequest.format) &&
        Objects.equals(delivery, reportRequest.delivery) &&
        Objects.equals(options, reportRequest.options);
  }

  @Override
  public int hashCode() {
    return Objects.hash(panelId, queryId, stampId, type, format, delivery, options);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportRequest {\n");
    
    sb.append("    panelId: ").append(toIndentedString(panelId)).append("\n");
    sb.append("    queryId: ").append(toIndentedString(queryId)).append("\n");
    sb.append("    stampId: ").append(toIndentedString(stampId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    format: ").append(toIndentedString(format)).append("\n");
    sb.append("    delivery: ").append(toIndentedString(delivery)).append("\n");
    sb.append("    options: ").append(toIndentedString(options)).append("\n");
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
