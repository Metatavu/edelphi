package fi.metatavu.edelphi.rest.translate;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

import fi.metatavu.edelphi.domainmodel.users.User;

/**
 * Abstract base class for all translators
 * 
 * @author Antti Leppä
 *
 * @param <J> JPA entity
 * @param <R> REST entity
 */
public abstract class AbstractTranslator<J, R> {

  /**
   * Translates JPA entity into REST entity
   * 
   * @param JPA entity
   * @return REST entity
   */
  public abstract R translate(J entity);

  /**
   * Translates date into offset date time
   * 
   * @param date
   * @return offset date time
   */
  protected OffsetDateTime translateDate(Date date) {
    if (date == null) {
      return null;
    }
    
    return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

  /**
   * Translates user into user id
   * 
   * @param user user
   * @return user id
   */
  protected Long translateUserId(User user) {
    if (user == null) {
      return null;
    }
    
    return user.getId();
  }
  
}
