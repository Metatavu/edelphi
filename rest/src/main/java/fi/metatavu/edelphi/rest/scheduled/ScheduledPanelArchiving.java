package fi.metatavu.edelphi.rest.scheduled;

import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.panels.PanelController;
import org.slf4j.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;

@Startup
@Singleton
public class ScheduledPanelArchiving {
  @Inject
  private PanelController panelController;

  @Inject
  private Logger logger;

  @Schedule (hour = "*", minute = "*/15")
  public void archive() {
    if (SchedulerUtils.panelArchivingScheduleActive()) {
      List<Panel> panelsToArchive = panelController.listPanelsToArchive(730, 1);

      for (Panel panelToArchive: panelsToArchive) {
        logger.info("Archiving panel: {} ({}), reason: ended and last modified at: {}", panelToArchive.getId(), panelToArchive.getRootFolder().getUrlName(), panelToArchive.getLastModified());
        panelController.archivePanel(panelToArchive);
      }
    }
  }

}
