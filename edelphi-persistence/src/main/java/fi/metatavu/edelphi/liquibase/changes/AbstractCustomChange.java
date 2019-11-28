package fi.metatavu.edelphi.liquibase.changes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
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


  /**
   * Returns next id from hibernate_sequences
   * 
   * @param database
   * @param entity
   * @return
   * @throws CustomChangeException
   */
  protected long getNextSequenceId(Database database, String entity) throws CustomChangeException {
    long id = 1;
    
    executeDelete(database, "DELETE FROM hibernate_sequences WHERE sequence_name = ?", entity);

    JdbcConnection connection = (JdbcConnection) database.getConnection();
    try (PreparedStatement statement = connection.prepareStatement(String.format("SELECT max(id) + 1 FROM %s", entity))) {
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          id = resultSet.getLong(1);
        }        
      }
    } catch (Exception e) {
      throw new CustomChangeException(e);
    }

    executeInsert(database, "INSERT INTO hibernate_sequences (sequence_next_hi_value, sequence_name) VALUES (?, ?)", id, entity);

    return id;
  }
  
  /**
   * Executes an insert SQL statement into the database
   * 
   * @param database database
   * @param sql SQL
   * @param params parameters
   * @return generated id
   * @throws CustomChangeException thrown when execution fails
   */
  protected long executeInsert(Database database, String sql, Object... params) throws CustomChangeException {
    JdbcConnection connection = (JdbcConnection) database.getConnection();
    
    try {
      PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      try {
        applyStatementParams(statement, params);
        statement.execute();
        
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
          return getGeneratedKey(generatedKeys);
        }
      } finally {
        statement.close();
      }
    } catch (Exception e) {
      throw new CustomChangeException(String.format("Failed to run insert sql: %s", sql), e);
    }
  }

  /**
   * Executes a delete SQL statement into the database
   * 
   * @param database database
   * @param sql SQL
   * @param params parameters
   * @throws CustomChangeException thrown when execution fails
   */
  protected void executeDelete(Database database,  String sql, Object... params) throws CustomChangeException {
    JdbcConnection connection = (JdbcConnection) database.getConnection();
    
    try {
      PreparedStatement statement = connection.prepareStatement(sql);
      try {
        applyStatementParams(statement, params);
        statement.execute();
      } finally {
        statement.close();
      }
    } catch (Exception e) {
      throw new CustomChangeException(String.format("Failed to run delete sql: %s", sql), e);
    }
  }

  /**
   * Applies SQL statement parameters
   * 
   * @param statement statement
   * @param params parameters
   * @throws SQLException thrown when setting fails
   */
  private void applyStatementParams(PreparedStatement statement, Object... params) throws SQLException {
    for (int i = 0, l = params.length; i < l; i++) {
      Object param = params[i];
      if (param instanceof List) {
        statement.setObject(i + 1, ((List<?>) param).toArray());
      } else {
        statement.setObject(i + 1, params[i]);
      }
    }
  }
  
  /**
   * Returns generated key from insert statement result set
   * 
   * @param generatedKeys insert statement result set
   * @return generated key
   * @throws SQLException thrown when returning fails
   */
  private long getGeneratedKey(ResultSet generatedKeys) throws SQLException {
    if (generatedKeys.next()) {
      return generatedKeys.getLong(1);
    }
    
    return -1;
  }

}
