package fi.metatavu.edelphi.batch;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Producer for job properties
 * 
 * @author Antti Leppä
 */
public class JobPropertyProducer {
  
  @Inject
  private JobContext jobContext;

  @Inject
  private StepContext stepContext;
  
  /**
   * Producer for job property
   * 
   * @param injectionPoint injection point
   * @return job property
   */
  @Produces
  @JobProperty
  public String produceJobPropertyString(InjectionPoint injectionPoint) {
    Member member = injectionPoint.getMember();
    String name = member.getName();
    return getProperty(name);
  }
  
  /**
   * Producer for job property
   * 
   * @param injectionPoint injection point
   * @return job property
   */
  @Produces
  @JobProperty
  public Long[] produceJobPropertyLongs(InjectionPoint injectionPoint) {
    Member member = injectionPoint.getMember();
    String name = member.getName();
    String value = getProperty(name);

    if (StringUtils.isEmpty(value)) {
      return null;
    }
    
    return Arrays.stream(StringUtils.split(value, ',')).map(Long::parseLong).toArray(Long[]::new);
  }

  /**
   * Producer for job property
   * 
   * @param injectionPoint injection point
   * @return job property
   */
  @Produces
  @JobProperty
  public Long produceJobPropertyLong(InjectionPoint injectionPoint) {
    Member member = injectionPoint.getMember();
    String name = member.getName();
    String value = getProperty(name);
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    
    return NumberUtils.createLong(value);
  }

  /**
   * Producer for job property
   * 
   * @param injectionPoint injection point
   * @return job property
   */
  @Produces
  @JobProperty
  public Locale produceJobPropertyLocale(InjectionPoint injectionPoint) {
    Member member = injectionPoint.getMember();
    String name = member.getName();
    return LocaleUtils.toLocale(getProperty(name));
  }

  /**
   * Producer for job property
   * 
   * @param injectionPoint injection point
   * @return job property
   */
  @Produces
  @JobProperty
  public UUID produceJobPropertyUUID(InjectionPoint injectionPoint) {
    Member member = injectionPoint.getMember();
    String name = member.getName();
    return UUID.fromString(getProperty(name));
  }

  /**
   * Returns step or job property by name
   * 
   * @param name name
   * @return property
   */
  private String getProperty(String name) {
    String stepProperty = getStepProperty(name);
    if (StringUtils.isNotEmpty(stepProperty)) {
      return stepProperty;
    }
    
    return getJobProperty(name);
  }
  
  /**
   * Returns job property by name
   * 
   * @param name name
   * @return job property
   */
  private String getJobProperty(String name) {
    Properties properties = jobContext.getProperties();
    return (String) properties.get(name);
  }
  
  /**
   * Returns step property by name
   * 
   * @param name name
   * @return job property
   */
  private String getStepProperty(String name) {
    if (stepContext == null) {
      return null;
    }
    
    Properties properties = stepContext.getProperties();
    return (String) properties.get(name);
  }

}
