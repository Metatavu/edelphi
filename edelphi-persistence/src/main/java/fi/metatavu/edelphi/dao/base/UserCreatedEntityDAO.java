package fi.metatavu.edelphi.dao.base;

import fi.metatavu.edelphi.domainmodel.users.User;

import java.util.List;

public interface UserCreatedEntityDAO<T> {
  List<T> listAllByCreator(User user);

  List<T> listAllByModifier(User user);

  T persist(T entity);
}
