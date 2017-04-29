package fi.metatavu.edelphi.dao.base;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiBulletin;
import fi.metatavu.edelphi.domainmodel.base.DelfoiBulletin_;
import fi.metatavu.edelphi.domainmodel.users.User;

public class DelfoiBulletinDAO extends GenericDAO<DelfoiBulletin> {
  
  public DelfoiBulletin create(Delfoi delfoi, String title, String message, User creator, Boolean important, Date importantEnds) {
    
    Date now = new Date();
    
    DelfoiBulletin bulletin = new DelfoiBulletin();
    bulletin.setArchived(Boolean.FALSE);
    bulletin.setCreated(now);
    bulletin.setCreator(creator);
    bulletin.setLastModified(now);
    bulletin.setLastModifier(creator);
    bulletin.setTitle(title);
    bulletin.setMessage(message);
    bulletin.setDelfoi(delfoi);
    bulletin.setImportant(important);
    bulletin.setImportantEnds(importantEnds);
    
    return persist(bulletin);
  }
  
  public List<DelfoiBulletin> listByDelfoiAndArchived(Delfoi delfoi, Boolean archived) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DelfoiBulletin> criteria = criteriaBuilder.createQuery(DelfoiBulletin.class);
    Root<DelfoiBulletin> root = criteria.from(DelfoiBulletin.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(DelfoiBulletin_.archived), archived),
        criteriaBuilder.equal(root.get(DelfoiBulletin_.delfoi), delfoi)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public DelfoiBulletin updateTitle(DelfoiBulletin bulletin, String title, User modifier) {
    EntityManager entityManager = getEntityManager();
    
    bulletin.setTitle(title);
    bulletin.setLastModifier(modifier);
    bulletin.setLastModified(new Date());
    
    entityManager.persist(bulletin);
    
    return bulletin;
  }

  public DelfoiBulletin updateMessage(DelfoiBulletin bulletin, String message, User modifier) {
    EntityManager entityManager = getEntityManager();
    
    bulletin.setMessage(message);
    bulletin.setLastModifier(modifier);
    bulletin.setLastModified(new Date());
    
    entityManager.persist(bulletin);
    
    return bulletin;
  }

  public DelfoiBulletin updateImportant(DelfoiBulletin bulletin, Boolean important) {
    bulletin.setImportant(important);
    return persist(bulletin);
  }

  public DelfoiBulletin updateImportantEnds(DelfoiBulletin bulletin, Date importantEnds) {
    bulletin.setImportantEnds(importantEnds);
    return persist(bulletin);
  }
  
}
