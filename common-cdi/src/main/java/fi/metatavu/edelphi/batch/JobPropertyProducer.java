package fi.metatavu.edelphi.batch;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

import javax.batch.runtime.context.JobContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class JobPropertyProducer {
  
  @Inject
  private JobContext jobContext;
  
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
    return getJobProperty(name);
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
    String value = getJobProperty(name);
    
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
    String value = getJobProperty(name);
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
    return LocaleUtils.toLocale(getJobProperty(name));
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
    return UUID.fromString(getJobProperty(name));
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

}
