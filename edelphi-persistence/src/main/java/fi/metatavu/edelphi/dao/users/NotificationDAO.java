package fi.metatavu.edelphi.dao.users;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;
import fi.metatavu.edelphi.domainmodel.users.Notification;
import fi.metatavu.edelphi.domainmodel.users.NotificationType;
import fi.metatavu.edelphi.domainmodel.users.Notification_;

public class NotificationDAO extends GenericDAO<Notification> {
  
  public Notification create(NotificationType type, String name, Long millisBefore, LocalizedEntry contentTemplate, LocalizedEntry subjectTemplate) {
    Notification notification = new Notification();
    
    notification.setName(name);
    notification.setType(type);
    notification.setMillisBefore(millisBefore);
    notification.setContentTemplate(contentTemplate);
    notification.setSubjectTemplate(subjectTemplate);
    
    return persist(notification);
  }

  public List<Notification> listByType(NotificationType type) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Notification> criteria = criteriaBuilder.createQuery(Notification.class);
    Root<Notification> root = criteria.from(Notification.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(Notification_.type), type)
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

}