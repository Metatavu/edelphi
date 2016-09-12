package fi.metatavu.edelphi.domainmodel.resources;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class Image extends Resource {
  
}
