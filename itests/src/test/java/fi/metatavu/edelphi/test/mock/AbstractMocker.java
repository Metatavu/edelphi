package fi.metatavu.edelphi.test.mock;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AbstractMocker {

  private static Logger logger = Logger.getLogger(AbstractMocker.class.getName());
  
  private List<Long> localizedEntryIds = new ArrayList<>();
  
  public AbstractMocker cleanup() {
    for (Long localizedEntryId : localizedEntryIds) {
      deleteLocalizedEntry(localizedEntryId);
    }
    
    return this;
  }
  
  protected Connection getDatabaseConnection() {
    String username = System.getProperty("it.jdbc.username");
    String password = System.getProperty("it.jdbc.password");
    String url = System.getProperty("it.jdbc.url");
    try {
      Class.forName(System.getProperty("it.jdbc.driver")).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "Failed to load JDBC driver", e);
      fail(e.getMessage());
    }

    try {
      return DriverManager.getConnection(url, username, password);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "Failed to get connection", e);
      fail(e.getMessage());
    }
    
    return null;
  }

  protected long getNextId(String sequence) {
    String selectSql = "select sequence_next_hi_value from hibernate_sequences where sequence_name = ?";
    String updateSql = "update hibernate_sequences set sequence_next_hi_value = ?  where sequence_name = ?";
    String insertSql = "insert into hibernate_sequences (sequence_next_hi_value, sequence_name) values (?, ?)";
    
    Long id = executeSqlLong(selectSql, sequence);
    if (id == null) {
      id = 1l;
      executeSql(insertSql, id + 1, sequence);
    } else {
      executeSql(updateSql, id + 1, sequence);
    }
    
    return id;
  }
  
  protected long createLocalizedEntry() {
    Long id = getNextId("LocalizedEntry");
    String sql = "INSERT INTO localizedEntry (id) VALUES (?)";
    executeSql(sql, id);
    localizedEntryIds.add(id);
    return id;
  }
  
  protected void deleteLocalizedEntry(Long id) {
    executeSql("DELETE FROM LocalizedValue WHERE entry_id = ?", id);
    executeSql("DELETE FROM LocalizedEntry WHERE id = ?", id);
  }

  protected long createLocalizedValue(String text, String locale, Long entryId) {
    Long id = getNextId("LocalizedValue");
    String sql = "INSERT INTO LocalizedValue (id, text, locale, entry_id) VALUES (?, ?, ?, ?)";
    executeSql(sql, id, text, locale, entryId);
    return id;
  }
  
  protected void executeSql(String sql, Object... params) {
    try (Connection connection = getDatabaseConnection()) {
      connection.setAutoCommit(true);
      PreparedStatement statement = connection.prepareStatement(sql);
      try {
        for (int i = 0, l = params.length; i < l; i++) {
          statement.setObject(i + 1, params[i]);
        }
        
        statement.execute();
      } finally {
        statement.close();
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to execute sql", e);
      fail(e.getMessage());
    }
  }

  protected Long executeSqlLong(String sql, Object... params) {
    try (Connection connection = getDatabaseConnection()) {
      connection.setAutoCommit(true);
      PreparedStatement statement = connection.prepareStatement(sql);
      try {
        for (int i = 0, l = params.length; i < l; i++) {
          statement.setObject(i + 1, params[i]);
        }
        
        statement.execute();
        
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()) {
          return resultSet.getLong(1);
        }
      } finally {
        statement.close();
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to execute sql", e);
      fail(e.getMessage());
    }
    
    return null;
  }

}
