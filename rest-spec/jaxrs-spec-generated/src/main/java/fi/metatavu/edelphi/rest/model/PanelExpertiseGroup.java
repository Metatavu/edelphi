package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class PanelExpertiseGroup   {
  private @Valid Long id = null;  private @Valid Long interestClassId = null;  private @Valid Long expertiseClassId = null;

  /**
   * Id
   **/
  public PanelExpertiseGroup id(Long id) {
    this.id = id;
    return this;
  }

  
  @ApiModelProperty(value = "Id")
  @JsonProperty("id")
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  /**
   **/
  public PanelExpertiseGroup interestClassId(Long interestClassId) {
    this.interestClassId = interestClassId;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("interestClassId")
  @NotNull
  public Long getInterestClassId() {
    return interestClassId;
  }
  public void setInterestClassId(Long interestClassId) {
    this.interestClassId = interestClassId;
  }

  /**
   **/
  public PanelExpertiseGroup expertiseClassId(Long expertiseClassId) {
    this.expertiseClassId = expertiseClassId;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("expertiseClassId")
  @NotNull
  public Long getExpertiseClassId() {
    return expertiseClassId;
  }
  public void setExpertiseClassId(Long expertiseClassId) {
    this.expertiseClassId = expertiseClassId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PanelExpertiseGroup panelExpertiseGroup = (PanelExpertiseGroup) o;
    return Objects.equals(id, panelExpertiseGroup.id) &&
        Objects.equals(interestClassId, panelExpertiseGroup.interestClassId) &&
        Objects.equals(expertiseClassId, panelExpertiseGroup.expertiseClassId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, interestClassId, expertiseClassId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PanelExpertiseGroup {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    interestClassId: ").append(toIndentedString(interestClassId)).append("\n");
    sb.append("    expertiseClassId: ").append(toIndentedString(expertiseClassId)).append("\n");
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
