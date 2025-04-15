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
  static boolean deletionSchedulersActive() {
    int schedulerStartHour = 0; // 00-01 (In FINNISH time 21 in UTC) archiving panels 01-02 deleting panels 02-03 archiving users 03-06 deleting users, panels every 15 minute, user archiving 1 minute, user deletion every 5 minute
    int schedulerEndHour = 6;
    int currentHour = ZonedDateTime.now(ZoneOffset.UTC).getHour();

    return currentHour >= schedulerStartHour || currentHour < schedulerEndHour;
  }

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
