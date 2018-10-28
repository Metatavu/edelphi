package fi.metatavu.edelphi.liquibase.changes;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

/**
 * Abstract base class for custom Liquibase changes
 * 
 * @author Antti Leppä
 */
public abstract class AbstractCustomChange implements CustomTaskChange {

  private StringBuilder confirmationMessage = new StringBuilder();
  
  /**
   * Appends string to confirmation message
   * 
   * @param message message
   */
  protected void appendConfirmationMessage(String message) {
    confirmationMessage.append(message);
  }

  @Override
  public String getConfirmationMessage() {
    return confirmationMessage.toString();
  }

  @Override
  public void setUp() throws SetupException {
    // No need to set anything up
  }

  @Override
  public void setFileOpener(ResourceAccessor resourceAccessor) {

  }

  @Override
  public ValidationErrors validate(Database database) {
    return null;
  }

}
