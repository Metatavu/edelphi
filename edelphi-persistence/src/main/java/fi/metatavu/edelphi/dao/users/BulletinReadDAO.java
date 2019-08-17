package fi.metatavu.edelphi.dao.users;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.base.Bulletin;
import fi.metatavu.edelphi.domainmodel.users.BulletinRead;
import fi.metatavu.edelphi.domainmodel.users.BulletinRead_;

@ApplicationScoped
public class BulletinReadDAO extends GenericDAO<BulletinRead> {
  
  public BulletinRead create(Bulletin bulletin, User user, Date readTime) {
    BulletinRead userNotification = new BulletinRead();
    
    userNotification.setBulletin(bulletin);
    userNotification.setUser(user);
    userNotification.setReadTime(readTime);

    return persist(userNotification);
  }

  public List<BulletinRead> listByBulletinAndUser(Bulletin bulletin, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BulletinRead> criteria = criteriaBuilder.createQuery(BulletinRead.class);
    Root<BulletinRead> root = criteria.from(BulletinRead.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(BulletinRead_.bulletin), bulletin),
        criteriaBuilder.equal(root.get(BulletinRead_.user), user)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  public Long countByBulletinAndUser(Bulletin bulletin, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<BulletinRead> root = criteria.from(BulletinRead.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(BulletinRead_.bulletin), bulletin),
        criteriaBuilder.equal(root.get(BulletinRead_.user), user)
      )
    );

    return entityManager.createQuery(criteria).getSingleResult();
  }

}