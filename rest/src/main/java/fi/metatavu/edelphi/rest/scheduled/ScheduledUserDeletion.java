package fi.metatavu.edelphi.rest.scheduled;

import fi.metatavu.edelphi.panels.PanelController;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class ScheduledUserDeletion {
    @Inject
    private PanelController panelController;

    @Schedule (hour = "*", minute = "*", second = "*/5", info = "Every 5 seconds timer")
    public void archive() {
    }

}
