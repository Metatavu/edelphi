package fi.metatavu.edelphi.rest.scheduled;

import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.panels.PanelController;

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

  @Schedule (hour = "*", minute = "*", second = "*/60", info = "Panel archiving scheduler. Runs every 60 seconds.")
  public void archive() {
    if (SchedulerUtils.deletionSchedulersActive()) {
      List<Panel> panelList = panelController.listPanelsToArchive(730, 1);

      if (!panelList.isEmpty()) {
        Panel panel = panelList.get(0);

        panelController.archivePanelScheduler(panel);
      }
    }
  }

}
