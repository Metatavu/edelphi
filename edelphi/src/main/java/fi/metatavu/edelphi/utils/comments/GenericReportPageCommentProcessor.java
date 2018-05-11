package fi.metatavu.edelphi.utils.comments;

import java.util.Map;

import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;

/**
 * Report generic page comment processor
 * 
 * @author Antti Leppä
 */
public class GenericReportPageCommentProcessor extends AbstractReportPageCommentProcessor {

  /**
   * Constructor for a comment processor
   * 
   * @param panelStamp panel stamp
   * @param queryPage query page
   * @param answers target map for answers
   */
  public GenericReportPageCommentProcessor(PanelStamp panelStamp, QueryPage queryPage, Map<Long, Map<String, String>> answers) {
    super(panelStamp, queryPage, answers);
  }

  @Override
  public void processComments() {
    // Nothing to do
  }
  
}
