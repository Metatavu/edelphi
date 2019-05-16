package fi.metatavu.edelphi.rest.model;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;


import io.swagger.annotations.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Panel   {
  private @Valid Long id = null;  private @Valid String name = null;  private @Valid String urlName = null;  private @Valid String description = null;  private @Valid PanelAccessLevel accessLevel = null;  private @Valid PanelState state = null;

  /**
   **/
  public Panel id(Long id) {
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
  public Panel name(String name) {
    this.name = name;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("name")
  @NotNull
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   **/
  public Panel urlName(String urlName) {
    this.urlName = urlName;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("urlName")
  public String getUrlName() {
    return urlName;
  }
  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  /**
   **/
  public Panel description(String description) {
    this.description = description;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   **/
  public Panel accessLevel(PanelAccessLevel accessLevel) {
    this.accessLevel = accessLevel;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("accessLevel")
  @NotNull
  public PanelAccessLevel getAccessLevel() {
    return accessLevel;
  }
  public void setAccessLevel(PanelAccessLevel accessLevel) {
    this.accessLevel = accessLevel;
  }

  /**
   **/
  public Panel state(PanelState state) {
    this.state = state;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("state")
  @NotNull
  public PanelState getState() {
    return state;
  }
  public void setState(PanelState state) {
    this.state = state;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Panel panel = (Panel) o;
    return Objects.equals(id, panel.id) &&
        Objects.equals(name, panel.name) &&
        Objects.equals(urlName, panel.urlName) &&
        Objects.equals(description, panel.description) &&
        Objects.equals(accessLevel, panel.accessLevel) &&
        Objects.equals(state, panel.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, urlName, description, accessLevel, state);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Panel {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    urlName: ").append(toIndentedString(urlName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    accessLevel: ").append(toIndentedString(accessLevel)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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
