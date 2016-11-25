package fi.metatavu.edelphi.liquibase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import liquibase.integration.cdi.CDILiquibaseConfig;
import liquibase.integration.cdi.annotations.LiquibaseType;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

@Dependent
public class LiquibaseProducer {
  
  @Resource (lookup = "java:jboss/datasources/edelphi")
  private DataSource dataSource;
  
  @Produces
  @LiquibaseType
  public CDILiquibaseConfig createConfig() {
    List<String> contexts = new ArrayList<>();
    
    if ("TEST".equals(System.getProperty("runmode"))) {
      contexts.add("test");
    }
    
    CDILiquibaseConfig config = new CDILiquibaseConfig();
    config.setChangeLog("fi/metatavu/edelphi/liquibase/changelog.xml");
    config.setContexts(StringUtils.join(contexts, ','));
    
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

}
