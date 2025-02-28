package fi.metatavu.edelphi.rest.scheduled;

import java.time.OffsetDateTime;

public class SchedulerUtils {

  /**
   * Scheduled deletion jobs are supposed to run only at night
   *
   * @return boolean that tells if now is the right time to run deletion schedulers
   */
  static boolean deletionSchedulersActive() {
    int schedulerStartHour = 22;
    int schedulerEndHour = 6;
    int currentHour = OffsetDateTime.now().getHour();

    return currentHour >= schedulerStartHour || currentHour < schedulerEndHour;
  }
}
