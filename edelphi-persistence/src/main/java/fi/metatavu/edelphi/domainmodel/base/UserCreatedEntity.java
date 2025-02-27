package fi.metatavu.edelphi.domainmodel.base;

import fi.metatavu.edelphi.domainmodel.users.User;

import java.lang.reflect.ParameterizedType;

public abstract class UserCreatedEntity {
  public abstract void setLastModifier(User user);

  public abstract void setCreator(User user);

  private Class<?> getGenericTypeClass() {
    ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
    return (Class<?>) parameterizedType.getActualTypeArguments()[0];
  }
}
