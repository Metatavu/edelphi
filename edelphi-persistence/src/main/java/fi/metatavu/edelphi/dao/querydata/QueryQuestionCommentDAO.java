package fi.metatavu.edelphi.dao.querydata;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment_;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply_;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage_;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection_;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Folder_;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.resources.Query_;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class QueryQuestionCommentDAO extends GenericDAO<QueryQuestionComment> {

  public QueryQuestionComment create(QueryReply queryReply, QueryPage queryPage, QueryQuestionComment parentComment, String comment, Boolean hidden, User creator) {
    Date now = new Date();
    return create(queryReply, queryPage, parentComment, null, comment, hidden, creator, now, creator, now);
  }

  @SuppressWarnings ("squid:S00107")
  public QueryQuestionComment create(QueryReply queryReply, QueryPage queryPage, QueryQuestionComment parentComment, QueryQuestionCommentCategory category, String comment, Boolean hidden, User creator, Date created, User modifier, Date modified) {
    QueryQuestionComment questionComment = new QueryQuestionComment();

    questionComment.setComment(comment);
    questionComment.setQueryPage(queryPage);
    questionComment.setQueryReply(queryReply);
    questionComment.setParentComment(parentComment);
    questionComment.setCreator(creator);
    questionComment.setHidden(hidden);
    questionComment.setCategory(category);
    questionComment.setCreated(created);
    questionComment.setLastModifier(modifier);
    questionComment.setLastModified(modified);
    
    getEntityManager().persist(questionComment);
    return questionComment;
  }
  
  public QueryQuestionComment findByQueryReplyAndQueryPage(QueryReply queryReply, QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryReply), queryReply),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria)); 
  }

  /**
   * Finds a root comment by query reply and query page
   * 
   * @param queryReply query reply
   * @param queryPage query page
   * @return root comment or null if not found
   */
  public QueryQuestionComment findRootCommentByQueryReplyAndQueryPage(QueryReply queryReply, QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryReply), queryReply),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
        criteriaBuilder.isNull(root.get(QueryQuestionComment_.parentComment)),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
      )
    );
    
    TypedQuery<QueryQuestionComment> query = entityManager.createQuery(criteria);
    query.setMaxResults(1);

    return getSingleResult(query); 
  }

  /**
   * Finds a root comment by query reply, query page and category
   * 
   * @param queryReply query reply
   * @param queryPage query page
   * @param category query comment category
   * @return root comment or null if not found
   */
  public QueryQuestionComment findRootCommentByQueryReplyQueryPageAndCategory(QueryReply queryReply, QueryPage queryPage, QueryQuestionCommentCategory category) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryReply), queryReply),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
        criteriaBuilder.isNull(root.get(QueryQuestionComment_.parentComment)),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.category), category),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
      )
    );
    
    TypedQuery<QueryQuestionComment> query = entityManager.createQuery(criteria);
    query.setMaxResults(1);

    return getSingleResult(query); 
  }
  
  /**
   * Lists all nonarchived comments left on the given query page across all the stamps of the panel
   * the query belongs to. You probably want to use {@link #listByQueryPageAndStamp(QueryPage,PanelStamp)}
   * instead.
   * 
   * @param queryPage  the comments' query page
   * 
   * @return  a list of all nonarchived comments left on the given query page across all stamps
   */
  public List<QueryQuestionComment> listByQueryPage(QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  

  /**
   * Lists comments by given parameters. All parameters are optional
   * 
   * @param queryPage filter by comment's query page
   * @param stamp filter by panel stamp
   * @param query filter by query. Ignored if null
   * @param queryParentFolder filter by query parent folder
   * @param parentComment filter by parent comment. Ignored if null
   * @param onlyRootComments return only root comments. 
   * @param user filter by user. Ignored if null
   * @param category return only comments of specified category. Ignored if null
   * @param onlyNullCategories return only comments without category
   * @param archived filter by archived. Ignored if null
   * @param firstResult first result
   * @param maxResults max results
   * @param oldestFirst sort by oldest first
   * @return a list of comments
   */
  public List<QueryQuestionComment> list(
    QueryPage queryPage,
    PanelStamp stamp,
    Query query,
    Folder queryParentFolder,
    QueryQuestionComment parentComment,
    boolean onlyRootComments,
    User user,
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory category,
    boolean onlyNullCategories,
    Boolean archived,
    Integer firstResult,
    Integer maxResults,
    boolean oldestFirst
  ) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    Join<QueryQuestionComment, QueryPage> queryPageJoin = root.join(QueryQuestionComment_.queryPage);
    Join<QueryPage, QuerySection> querySectionJoin = queryPageJoin.join(QueryPage_.querySection);
    Join<QueryQuestionComment, QueryReply> queryReplyJoin = root.join(QueryQuestionComment_.queryReply);
    Join<QuerySection, Query> queryJoin = querySectionJoin.join(QuerySection_.query);
    Join<Query, Folder> queryFolderJoin = queryJoin.join(Query_.parentFolder);
    
    List<Predicate> criterias = new ArrayList<>();
    
    if (queryPage != null) {
      criterias.add(criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage));
    }
    
    if (stamp != null) {
      criterias.add(criteriaBuilder.equal(queryReplyJoin.get(QueryReply_.stamp), stamp));
    }
    
    if (query != null) {
      criterias.add(criteriaBuilder.equal(querySectionJoin.get(QuerySection_.query), query));
    }
    
    if (queryParentFolder != null) {
      criterias.add(criteriaBuilder.equal(queryJoin.get(Query_.parentFolder), queryParentFolder));
    }
    
    if (onlyRootComments) {
      criterias.add(criteriaBuilder.isNull(root.get(QueryQuestionComment_.parentComment)));      
    } else if (parentComment != null) {
      criterias.add(criteriaBuilder.equal(root.get(QueryQuestionComment_.parentComment), parentComment)); 
    }
    
    if (onlyNullCategories) {
      criterias.add(criteriaBuilder.isNull(root.get(QueryQuestionComment_.category)));      
    } else if (category != null) {
      criterias.add(criteriaBuilder.equal(root.get(QueryQuestionComment_.category), category)); 
    }
    
    if (user != null) {
      criterias.add(criteriaBuilder.equal(queryReplyJoin.get(QueryReply_.user), user));
    }
    
    if (archived != null) {
      criterias.add(criteriaBuilder.equal(queryJoin.get(Query_.archived), archived));
      criterias.add(criteriaBuilder.equal(queryReplyJoin.get(QueryReply_.archived), archived));
      criterias.add(criteriaBuilder.equal(queryPageJoin.get(QueryPage_.archived), archived));
      criterias.add(criteriaBuilder.equal(querySectionJoin.get(QuerySection_.archived), archived));
      criterias.add(criteriaBuilder.equal(queryFolderJoin.get(Folder_.archived), archived));
      criterias.add(criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), archived));
    }
    
    criteria.select(root);
    criteria.where(criteriaBuilder.and(criterias.toArray(new Predicate[0])));

    if (oldestFirst) {
      criteria.orderBy(criteriaBuilder.asc(root.get(QueryQuestionComment_.created)));
    } else {
      criteria.orderBy(criteriaBuilder.desc(root.get(QueryQuestionComment_.created)));
    }

    TypedQuery<QueryQuestionComment> typedQuery = entityManager.createQuery(criteria);
    typedQuery.setFirstResult(firstResult);
    typedQuery.setMaxResults(maxResults);

    return typedQuery.getResultList();
  }

  /**
   * Counts comments by given parameters. All parameters are optional
   *
   * @param queryPage filter by comment's query page
   * @param stamp filter by panel stamp
   * @param query filter by query. Ignored if null
   * @param queryParentFolder filter by query parent folder
   * @param parentComment filter by parent comment. Ignored if null
   * @param onlyRootComments return only root comments.
   * @param user filter by user. Ignored if null
   * @param category return only comments of specified category. Ignored if null
   * @param onlyNullCategories return only comments without category
   * @param archived filter by archived. Ignored if null
   * @return count of comments
   */
  public Long count(
    QueryPage queryPage,
    PanelStamp stamp,
    Query query,
    Folder queryParentFolder,
    QueryQuestionComment parentComment,
    boolean onlyRootComments,
    User user,
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory category,
    boolean onlyNullCategories,
    Boolean archived
  ) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);

    Join<QueryQuestionComment, QueryPage> queryPageJoin = root.join(QueryQuestionComment_.queryPage);
    Join<QueryPage, QuerySection> querySectionJoin = queryPageJoin.join(QueryPage_.querySection);
    Join<QueryQuestionComment, QueryReply> queryReplyJoin = root.join(QueryQuestionComment_.queryReply);
    Join<QuerySection, Query> queryJoin = querySectionJoin.join(QuerySection_.query);
    Join<Query, Folder> queryFolderJoin = queryJoin.join(Query_.parentFolder);

    List<Predicate> criterias = new ArrayList<>();

    if (queryPage != null) {
      criterias.add(criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage));
    }

    if (stamp != null) {
      criterias.add(criteriaBuilder.equal(queryReplyJoin.get(QueryReply_.stamp), stamp));
    }

    if (query != null) {
      criterias.add(criteriaBuilder.equal(querySectionJoin.get(QuerySection_.query), query));
    }

    if (queryParentFolder != null) {
      criterias.add(criteriaBuilder.equal(queryJoin.get(Query_.parentFolder), queryParentFolder));
    }

    if (onlyRootComments) {
      criterias.add(criteriaBuilder.isNull(root.get(QueryQuestionComment_.parentComment)));
    } else if (parentComment != null) {
      criterias.add(criteriaBuilder.equal(root.get(QueryQuestionComment_.parentComment), parentComment));
    }

    if (onlyNullCategories) {
      criterias.add(criteriaBuilder.isNull(root.get(QueryQuestionComment_.category)));
    } else if (category != null) {
      criterias.add(criteriaBuilder.equal(root.get(QueryQuestionComment_.category), category));
    }

    if (user != null) {
      criterias.add(criteriaBuilder.equal(queryReplyJoin.get(QueryReply_.user), user));
    }

    if (archived != null) {
      criterias.add(criteriaBuilder.equal(queryJoin.get(Query_.archived), archived));
      criterias.add(criteriaBuilder.equal(queryReplyJoin.get(QueryReply_.archived), archived));
      criterias.add(criteriaBuilder.equal(queryPageJoin.get(QueryPage_.archived), archived));
      criterias.add(criteriaBuilder.equal(querySectionJoin.get(QuerySection_.archived), archived));
      criterias.add(criteriaBuilder.equal(queryFolderJoin.get(Folder_.archived), archived));
      criterias.add(criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), archived));
    }

    criteria.select(criteriaBuilder.count(root));
    criteria.where(criteriaBuilder.and(criterias.toArray(new Predicate[0])));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  /**
   * Lists all nonarchived comments left on the given query page in the given panel stamp.
   * 
   * @param queryPage  the comments' query page
   * @param stamp      the panel stamp
   * 
   * @return  a list of all nonarchived comments left on the given query page in the given panel stamp
   */
  public List<QueryQuestionComment> listByQueryPageAndStamp(QueryPage queryPage, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    Join<QueryQuestionComment, QueryReply> qqJoin = root.join(QueryQuestionComment_.queryReply);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(qqJoin.get(QueryReply_.stamp), stamp),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
        criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  /**
   * Lists all comments by query page (including archived)
   *
   * @param queryPage query page
   * @return comments
   */
  public List<QueryQuestionComment> listAllByQueryPage(QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage));

    return entityManager.createQuery(criteria).getResultList(); 
  }

  /**
   * Lists all comments by query reply (including archived)
   *
   * @param queryReply query reply
   * @return comments
   */
  public List<QueryQuestionComment> listAllByReply(QueryReply queryReply) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(QueryQuestionComment_.queryReply), queryReply));

    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Lists all comments by query (including archived)
   *
   * @param query query
   * @return comments
   */
  public List<QueryQuestionComment> listAllByQuery(Query query) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    Join<QueryQuestionComment, QueryReply> qqJoin = root.join(QueryQuestionComment_.queryReply);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(qqJoin.get(QueryReply_.query), query));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryQuestionComment> listRootCommentsByQueryPageAndStampOrderByCreated(QueryPage queryPage, PanelStamp panelStamp) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    Join<QueryQuestionComment, QueryReply> qqJoin = root.join(QueryQuestionComment_.queryReply);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(qqJoin.get(QueryReply_.stamp), panelStamp),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE),
            criteriaBuilder.isNull(root.get(QueryQuestionComment_.parentComment))
        )
    );
    
    criteria.orderBy(criteriaBuilder.asc(root.get(QueryQuestionComment_.created)));

    return entityManager.createQuery(criteria).getResultList(); 
  }

  public List<QueryQuestionComment> listByParentCommentAndArchived(QueryQuestionComment parentComment, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryQuestionComment_.parentComment), parentComment),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), archived)
        )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  /**
   * Lists all non-root comments on page and orders them to map with parentComment.id as key and 
   * list of comments directly below parentComment as value. 
   *  
   * @param queryPage page where to list the comments from
   * @return list of non-root comments on page and orders to mapped by parentComment.id
   */
  public Map<Long, List<QueryQuestionComment>> listTreesByQueryPage(QueryPage queryPage) {
    Map<Long, List<QueryQuestionComment>> result = new HashMap<>();

    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE),
            criteriaBuilder.isNotNull(root.get(QueryQuestionComment_.parentComment))
        )
    );

    List<QueryQuestionComment> allChildren = entityManager.createQuery(criteria).getResultList(); 
    
    for (QueryQuestionComment comment : allChildren) {
      Long parentCommentId = comment.getParentComment().getId();
      List<QueryQuestionComment> children = result.get(parentCommentId);
      
      if (children == null) {
        children = new ArrayList<>();
        result.put(parentCommentId, children);
      }

      children.add(comment);
    }
    
    return result;
  }
  
  public Long countByQueryPage(QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE)
        )
    );

    return entityManager.createQuery(criteria).getSingleResult();
  }

  public QueryQuestionComment updateComment(QueryQuestionComment comment, String newComment, User modifier) {
    comment.setLastModified(new Date());
    comment.setLastModifier(modifier);
    comment.setComment(newComment);
    
    getEntityManager().persist(comment);
    return comment;
  }

  public QueryQuestionComment updateComment(QueryQuestionComment comment, String newComment, User modifier, Date modified) {
    comment.setLastModified(modified);
    comment.setLastModifier(modifier);
    comment.setComment(newComment);
    
    getEntityManager().persist(comment);
    return comment;
  }

  public QueryQuestionComment updateCategory(QueryQuestionComment comment, QueryQuestionCommentCategory category, User modifier, Date modified) {
    comment.setLastModified(modified);
    comment.setLastModifier(modifier);
    comment.setCategory(category);    
    return persist(comment);
  }

  public QueryQuestionComment updateHidden(QueryQuestionComment comment, Boolean hidden, User modifier) {
    comment.setLastModified(new Date());
    comment.setLastModifier(modifier);
    comment.setHidden(hidden);
    
    getEntityManager().persist(comment);
    
    return comment;
  }

  public Map<Long, List<QueryQuestionComment>> listTreesByQueryPageAndStampOrderByCreated(QueryPage queryPage, PanelStamp panelStamp) {
    Map<Long, List<QueryQuestionComment>> result = new HashMap<>();

    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    Join<QueryQuestionComment, QueryReply> qqJoin = root.join(QueryQuestionComment_.queryReply);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(qqJoin.get(QueryReply_.stamp), panelStamp),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.queryPage), queryPage),
            criteriaBuilder.equal(root.get(QueryQuestionComment_.archived), Boolean.FALSE),
            criteriaBuilder.isNotNull(root.get(QueryQuestionComment_.parentComment))
        )
    );
    criteria.orderBy(criteriaBuilder.asc(root.get(QueryQuestionComment_.created)));

    List<QueryQuestionComment> allChildren = entityManager.createQuery(criteria).getResultList();

    for (QueryQuestionComment comment : allChildren) {
      Long parentCommentId = comment.getParentComment().getId();
      List<QueryQuestionComment> children = result.get(parentCommentId);

      if (children == null) {
        children = new ArrayList<>();
        result.put(parentCommentId, children);
      }

      children.add(comment);
    }

    return result;
  }

  public List<QueryQuestionComment> listAllByCreator(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionComment_.creator), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryQuestionComment> listAllByModifier(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionComment> criteria = criteriaBuilder.createQuery(QueryQuestionComment.class);
    Root<QueryQuestionComment> root = criteria.from(QueryQuestionComment.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionComment_.lastModifier), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
}
