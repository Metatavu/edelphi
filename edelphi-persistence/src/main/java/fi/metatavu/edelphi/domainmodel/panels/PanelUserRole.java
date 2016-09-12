package fi.metatavu.edelphi.domainmodel.panels;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import fi.metatavu.edelphi.domainmodel.users.UserRole;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class PanelUserRole extends UserRole {
}
