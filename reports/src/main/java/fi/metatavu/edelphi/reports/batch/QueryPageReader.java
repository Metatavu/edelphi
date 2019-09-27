package fi.metatavu.edelphi.reports.batch;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemReader;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.reports.ReportException;

/**
 * Batch item reader for reading query pages
 * 
 * @author Antti Leppä
 */
@Named
public class QueryPageReader extends TypedItemReader<QueryPage> {
  
  @Inject
  private Logger logger;

  @Inject
  private QueryController queryController; 

  @Inject
  @JobProperty
  private Long[] pageIds;
  
  private int index;
  
  @Override
  public void open(Serializable checkpoint) throws Exception {
    super.open(checkpoint);
    index = 0;
    logger.info("Reading {} query pages for report", pageIds != null ? pageIds.length : 0);
  }

  @Override
  public QueryPage read() throws Exception {
    if (this.pageIds == null) {
      return null;
    }
    
    try {
      if (this.index < this.pageIds.length) {
        Long pageId = this.pageIds[this.index];
        QueryPage queryPage = queryController.findQueryPageById(pageId);
        if (queryPage == null) {
          throw new ReportException(String.format("Could not find query page by id %d", pageId));
        } else {
          return queryPage;
        }
      }
      
      return null;
    } finally {
      this.index++;
    }
  }
  
}
