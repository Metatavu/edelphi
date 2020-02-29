package fi.metatavu.edelphi.queries.batch;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemReader;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.queries.QueryController;

/**
 * Batch item reader for reading single query
 * 
 * @author Antti Leppä
 */
@Named
public class QueryReader extends TypedItemReader<Query> {
  
  @Inject
  private Logger logger;

  @Inject
  private QueryController queryController; 

  @Inject
  @JobProperty
  private Long queryId;
  
  private int index;
  
  @Override
  public void open(Serializable checkpoint) throws Exception {
    super.open(checkpoint);
    logger.info("Reading {} query", queryId);
    index = 0;
  }

  @Override
  public Query read() throws Exception {
    try {
      if (this.queryId == null || index > 0) {
        return null;
      }
      
      return queryController.findQueryById(queryId);
    } finally {
      index++;
    }
  }
  
}
