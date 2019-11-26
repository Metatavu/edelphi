package fi.metatavu.edelphi.liquibase.changes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;

/**
 * Removes invitations from users that are already accepted invitations
 * 
 * @author Antti Leppä
 */
public class FixInvitations extends AbstractCustomChange {

  @Override
  public void execute(Database database) throws CustomChangeException {
    JdbcConnection connection = (JdbcConnection) database.getConnection();

    try (PreparedStatement statement = connection.prepareStatement("SELECT id, panel_id, email FROM PANELINVITATION")) {
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Long id = resultSet.getLong(1);
          Long panelId = resultSet.getLong(2);
          String email = resultSet.getString(3);
          
          if (isPanelUser(connection, panelId, email)) {
            deleteInvitation(connection, id);
            appendConfirmationMessage(String.format("Removed invitation to panel %d from email %s\n", panelId, email));
          }
        }
      }
    } catch (Exception e) {
      throw new CustomChangeException(e);
    }  
  }
  
  /**
   * Returns whether user by email is a PanelUser or not
   * 
   * @param connection connection
   * @param panelId panel id
   * @param email email
   * @return whether user by email is a PanelUser or not
   * @throws CustomChangeException on error
   */
  private boolean isPanelUser(JdbcConnection connection, Long panelId, String email) throws CustomChangeException {
    try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM PANELUSER WHERE panel_id = ? AND user_id = (SELECT id FROM USEREMAIL WHERE address = ?)")) {
      statement.setLong(1, panelId);
      statement.setString(2, email);
      
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next();
      }
    } catch (Exception e) {
      throw new CustomChangeException(e);
    }
  }

  /**
   * Deletes an invitation
   * 
   * @param connection connection
   * @param id invitation id
   * @throws CustomChangeException on error 
   */
  private void deleteInvitation(JdbcConnection connection, Long id) throws CustomChangeException {
    try (PreparedStatement statement = connection.prepareStatement("DELETE FROM PANELINVITATION WHERE id = ?")) {
      statement.setLong(1, id);
      statement.execute();
    } catch (Exception e) {
      throw new CustomChangeException(e);
    }
  }

}
