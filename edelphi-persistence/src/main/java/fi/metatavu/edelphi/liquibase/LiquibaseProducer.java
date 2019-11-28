package fi.metatavu.edelphi.liquibase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import liquibase.integration.cdi.CDILiquibaseConfig;
import liquibase.integration.cdi.annotations.LiquibaseType;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

@Dependent
public class LiquibaseProducer {
  
  private static Logger logger = Logger.getLogger(LiquibaseProducer.class.getName());
  
  @Resource (lookup = "java:jboss/datasources/edelphi")
  private DataSource dataSource;
  
  @Produces
  @LiquibaseType
  public CDILiquibaseConfig createConfig() {
    List<String> contextList = new ArrayList<>();
    
    if ("TEST".equals(getRunMode())) {
      contextList.add("test");
    } else {
      contextList.add("production");
    }
    
    String contexts = StringUtils.join(contextList, ',');
    
    CDILiquibaseConfig config = new CDILiquibaseConfig();
    config.setChangeLog("fi/metatavu/edelphi/liquibase/changelog.xml");
    config.setContexts(contexts);
    
    logger.info(String.format("Using contexts %s", contexts));
    
    return config;
  }
  
  @Produces
  @LiquibaseType
  public DataSource createDataSource() throws SQLException {
    return dataSource;
  }
  
  @Produces
  @LiquibaseType
  public ResourceAccessor create() {
    return new ClassLoaderResourceAccessor(getClass().getClassLoader());
  }
  
  /**
   * Returns system's current run mode
   * 
   * @return system's current run mode
   */
  private String getRunMode() {
    String result = System.getProperty("runmode");
    if (StringUtils.isNotBlank(result)) {
      return result;
    }
    
    return System.getenv("runmode");
  }

}
