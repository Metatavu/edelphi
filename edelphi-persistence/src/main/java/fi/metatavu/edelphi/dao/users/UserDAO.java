package fi.metatavu.edelphi.dao.users;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.domainmodel.users.User_;
import fi.metatavu.edelphi.search.SearchResult;

public class UserDAO extends GenericDAO<User> {
  
  @SuppressWarnings ("squid:S00107")
  public User create(String firstName, String lastName, String nickname, User creator, SubscriptionLevel subscriptionLevel, Date subscriptionStarted, Date subscriptionEnds, String locale) {
    Date now = new Date();
    
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setNickname(nickname);
    user.setCreated(now);
    user.setCreator(creator);
    user.setLastModified(now);
    user.setLastModifier(creator);
    user.setArchived(Boolean.FALSE);
    user.setSubscriptionLevel(subscriptionLevel);
    user.setSubscriptionStarted(subscriptionStarted);
    user.setSubscriptionEnds(subscriptionEnds);
    user.setLocale(locale);
    
    return persist(user);
  }

  public List<User> listByNeSubscriptionLevelAndSubscriptionEndsBefore(SubscriptionLevel subscriptionLevelNot, Date subscriptionEndsBefore) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);
    Root<User> root = criteria.from(User.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.notEqual(root.get(User_.subscriptionLevel), subscriptionLevelNot),
          criteriaBuilder.lessThanOrEqualTo(root.get(User_.subscriptionEnds), subscriptionEndsBefore)
        )
      );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  @SuppressWarnings("unchecked")
  public SearchResult<User> searchByFullName(int resultsPerPage, int page, String searchText) {
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(getEntityManager());

    int firstResult = page * resultsPerPage;
    
    String criterial = QueryParser.escape(searchText);
    criterial = criterial.replace(" ", "\\ ");
    criterial = criterial + "*";

    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("+fullNameSearch:");
    queryBuilder.append(criterial);
    queryBuilder.append(" +archived:false +emails.id:[* TO 9999999]");
  
    try {
      String queryString = queryBuilder.toString();
      QueryParser parser = new QueryParser("", new StandardAnalyzer());
      org.apache.lucene.search.Query luceneQuery = parser.parse(queryString);

      FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, User.class)
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;
      
      return new SearchResult<>(page, pages, hits, firstResult, lastResult, query.getResultList());

    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public SearchResult<User> searchByNameOrEmail(int resultsPerPage, int page, String searchText) {
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(getEntityManager());

    int firstResult = page * resultsPerPage;
    
    String criteria = QueryParser.escape(searchText);
    criteria = criteria.replace(" ", "\\ ");
    criteria = criteria + "*";

    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("+(firstName:");
    queryBuilder.append(criteria);
    queryBuilder.append(" lastName:");
    queryBuilder.append(criteria);
    queryBuilder.append(" emails.address:");
    queryBuilder.append(criteria + ")");
    queryBuilder.append(" +archived:false +emails.id:[* TO 9999999]");
  
    try {
      String queryString = queryBuilder.toString();
      org.apache.lucene.search.Query luceneQuery;
      QueryParser parser = new QueryParser("", new StandardAnalyzer());
      luceneQuery = parser.parse(queryString);

      FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, User.class)
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;
      
      return new SearchResult<>(page, pages, hits, firstResult, lastResult, query.getResultList());

    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }
  
  public User update(User user, String firstName, String lastName, String nickname, User modifier) {
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setNickname(nickname);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }

  public User updateFirstName(User user, String firstName, User modifier) {
    user.setFirstName(firstName);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }
  
  public User updateLastLogin(User user, Date lastLogin) {
    user.setLastLogin(lastLogin);
    getEntityManager().persist(user);
    return user;
  }

  public User updateLastName(User user, String lastName,  User modifier) {
    user.setLastName(lastName);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }

  public User updateNickname(User user, String nickname, User modifier) {
    user.setNickname(nickname);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }

  public User updateDefaultEmail(User user, UserEmail userEmail, User modifier) {
    user.setDefaultEmail(userEmail);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }
  
  public User addUserEmail(User user, UserEmail userEmail, boolean defaultEmail, User modifier) {
    user.addEmail(userEmail);
    if (defaultEmail) {
      user.setDefaultEmail(userEmail);
    }
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }
  
  public User removeUserEmail(User user, UserEmail userEmail, User modifier) {
    user.removeEmail(userEmail);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);
    getEntityManager().persist(user);
    return user;
  }

  public User updateSubscriptionLevel(User user, SubscriptionLevel subscriptionLevel) {
    user.setSubscriptionLevel(subscriptionLevel);
    return persist(user);
  }

  public User updateSubscriptionStarted(User user, Date subscriptionStarted) {
    user.setSubscriptionStarted(subscriptionStarted);
    return persist(user);
  }

  public User updateSubscriptionEnds(User user, Date subscriptionEnds) {
    user.setSubscriptionEnds(subscriptionEnds);
    return persist(user);
  }

  public User updatePlan(User user, Plan plan) {
    user.setPlan(plan);
    return persist(user);
  }

  public User updateLocale(User user, String locale) {
    user.setLocale(locale);
    return persist(user);
  }
  
}