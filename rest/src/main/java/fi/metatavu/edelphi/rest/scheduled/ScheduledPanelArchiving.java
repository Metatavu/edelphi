package fi.metatavu.edelphi.rest.scheduled;

import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
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

    @Schedule (hour = "*", minute = "*", second = "*/30", info = "Every 5 seconds timer")
    public void archive() {
        List<Panel> panels =  panelController.listPanelsToArchive(PanelState.ENDED, 0, 1000);

        System.out.println("Amount of ended panels: " + panels.size());

        List<Panel> panelList = panelController.listPanelsToArchive(PanelState.ENDED, 0, 1);

        if (!panelList.isEmpty()) {
            Panel panel = panelList.get(0);

            panelController.archivePanelByScheduler(panel);
        }
    }

}
