package fi.metatavu.edelphi.dao.users;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.users.Notification;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserNotification;
import fi.metatavu.edelphi.domainmodel.users.UserNotification_;

public class UserNotificationDAO extends GenericDAO<UserNotification> {
  
  public UserNotification create(Notification notification, User user, Date notificationSent) {
    UserNotification userNotification = new UserNotification();
    
    userNotification.setNotification(notification);
    userNotification.setUser(user);
    userNotification.setNotificationSent(notificationSent);
    
    return persist(userNotification);
  }

  public List<UserNotification> listByNotificationAndUser(Notification notification, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserNotification> criteria = criteriaBuilder.createQuery(UserNotification.class);
    Root<UserNotification> root = criteria.from(UserNotification.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(UserNotification_.notification), notification),
        criteriaBuilder.equal(root.get(UserNotification_.user), user)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  public Long countByNotificationAndUser(Notification notification, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<UserNotification> root = criteria.from(UserNotification.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(UserNotification_.notification), notification),
        criteriaBuilder.equal(root.get(UserNotification_.user), user)
      )
    );

    return entityManager.createQuery(criteria).getSingleResult();
  }
}