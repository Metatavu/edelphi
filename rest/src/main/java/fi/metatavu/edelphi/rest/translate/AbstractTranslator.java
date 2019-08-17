package fi.metatavu.edelphi.rest.translate;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang3.EnumUtils;

import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.users.UserController;

/**
 * Abstract base class for all translators
 * 
 * @author Antti Leppä
 *
 * @param <J> JPA entity
 * @param <R> REST entity
 */
public abstract class AbstractTranslator<J, R> {

  @Inject
  private UserController userController;
  
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
  protected UUID translateUserId(User user) {
    if (user == null) {
      return null;
    }
    
    return userController.getUserKeycloakId(user);
  }

  /**
   * Translates enum from on to another
   * 
   * @param <E> target enum
   * @param enumClass target enum class
   * @param original original enum
   * @return translated enum
   */
  protected <E extends Enum<E>> E translateEnum(final Class<E> enumClass, Enum<?> original) {
    if (original == null) {
      return null;
    }
    
    return EnumUtils.getEnum(enumClass, original.name());
  }
  
}
