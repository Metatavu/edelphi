package fi.metatavu.edelphi.rest.scheduled;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Scheduled deletion jobs are supposed to run only at night
 *
 */
public class SchedulerUtils {
  static boolean panelArchivingScheduleActive() {
    int schedulerStartHour = 21;
    int currentHour = ZonedDateTime.now(ZoneOffset.UTC).getHour();
    return currentHour == schedulerStartHour;
  }

  static boolean panelDeletionScheduleActive() {
    int schedulerStartHour = 22;
    int currentHour = ZonedDateTime.now(ZoneOffset.UTC).getHour();
    return currentHour == schedulerStartHour;
  }

  static boolean userArchivingScheduleActive() {
    int schedulerStartHour = 23;
    int currentHour = ZonedDateTime.now(ZoneOffset.UTC).getHour();
    return currentHour == schedulerStartHour;
  }

  static boolean userDeletionSchedulerActive() {
    int schedulerEndHour = 3;
    int currentHour = ZonedDateTime.now(ZoneOffset.UTC).getHour();

    return currentHour < schedulerEndHour;
  }
}
