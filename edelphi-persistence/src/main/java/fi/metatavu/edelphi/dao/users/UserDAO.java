package fi.metatavu.edelphi.dao.users;

import java.util.Date;

import javax.persistence.PersistenceException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.hibernate.search.jpa.Search;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.search.SearchResult;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;

public class UserDAO extends GenericDAO<User> {
  
  public User create(String firstName, String lastName, String nickname, User creator, SubscriptionLevel subscriptionLevel, Date subscriptionStarted, Date subscriptionEnds) {
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

    return persist(user);
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

}