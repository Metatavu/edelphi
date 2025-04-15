package fi.metatavu.edelphi.rest.scheduled;

import java.time.OffsetDateTime;

/**
 * Scheduled deletion jobs are supposed to run only at night
 *
 */
public class SchedulerUtils {
  static boolean deletionSchedulersActive() {
    int schedulerStartHour = 22;
    int schedulerEndHour = 6;
    int currentHour = OffsetDateTime.now().getHour();

    return currentHour >= schedulerStartHour || currentHour < schedulerEndHour;
  }
}
