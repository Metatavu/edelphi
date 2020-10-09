package fi.metatavu.edelphi.reports.batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.batch.api.AbstractBatchlet;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;

/**
 * Abstract base class for batch result printers
 * 
 * @author Antti Leppä
 */
public abstract class AbstractPrinter extends AbstractBatchlet {

  @Inject
  private ReportMessages reportMessages;

  @Inject
  private PanelController panelController;

  @Inject
  @JobProperty
  private Long queryId;

  @Inject
  @JobProperty
  private String baseUrl;

  @Inject
  @JobProperty
  private Long[] pageIds;

  @Inject
  @JobProperty
  private String deliveryEmail;

  @Inject
  @JobProperty
  private Locale locale;

  @Inject
  @JobProperty
  private Long[] expertiseGroupIds;

  @Inject
  @JobProperty
  private Long[] panelUserGroupIds;

  /**
   * Returns export filters as human readable text
   * 
   * @return export filters as human readable text
   */
  protected String getFilters() {
    List<String> filters = new ArrayList<>();
    
    if (expertiseGroupIds != null) {    
      String expertiseGroupNames = Arrays.stream(expertiseGroupIds)
          .map(panelController::findPanelUserExpertiseGroup)
          .map(this::getExpertiseGroupName)
          .collect(Collectors.joining(", "));
      
      filters.add(reportMessages.getText(locale, "reports.mail.expertiseFilter", expertiseGroupNames));
    }
    
    if (panelUserGroupIds != null) {    
      String panelUserGroupNames = Arrays.stream(panelUserGroupIds)
        .map(panelController::findPanelUserGroup)
        .map(PanelUserGroup::getName)
        .collect(Collectors.joining(", "));
      
      filters.add(reportMessages.getText(locale, "reports.mail.panelUserGroupFilter", panelUserGroupNames));
    }
    
    if (filters.isEmpty()) {    
      return reportMessages.getText(locale, "reports.mail.noFilters");
    } else {
      return StringUtils.join(filters, ", ");
    }
  }
  
  /**
   * Returns export options as human readable text
   * 
   * @return export options as human readable text
   */
  protected String getOptions() {
    return reportMessages.getText(locale, "reports.mail.noSpecifiedOptions");
  }

  /**
   * Return name for given expertise group
   * 
   * @param expertiseGroup expertise group
   * @return name for the group
   */
  protected String getExpertiseGroupName(PanelUserExpertiseGroup expertiseGroup) {
    PanelUserIntressClass intressClass = expertiseGroup.getIntressClass();
    PanelUserExpertiseClass expertiseClass = expertiseGroup.getExpertiseClass();
    return String.format("%s / %s", intressClass.getName(), expertiseClass.getName());
  }
  
}
