package fr.routine.dujour;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/** Programme l'alarme de minuit (remise à zéro) et l'alarme de rappel. */
final class Scheduler {
    private static final int RC_REMINDER = 1;
    private static final int RC_MIDNIGHT = 2;

    private Scheduler() {}

    static void scheduleAll(Context c) {
        scheduleMidnight(c);
        scheduleReminder(c);
    }

    static void scheduleMidnight(Context c) {
        ZonedDateTime t = LocalDate.now().plusDays(1)
                .atStartOfDay(ZoneId.systemDefault()).plusSeconds(5);
        setAlarm(c, t.toInstant().toEpochMilli(), pending(c, AlarmReceiver.ACTION_MIDNIGHT, RC_MIDNIGHT));
    }

    static void scheduleReminder(Context c) {
        PendingIntent pi = pending(c, AlarmReceiver.ACTION_REMINDER, RC_REMINDER);
        c.getSystemService(AlarmManager.class).cancel(pi);

        int mode = Prefs.mode(c);
        if (mode == Prefs.MODE_WIDGET) return;

        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime todayAt = now.toLocalDate()
                .atTime(Prefs.hour(c), Prefs.minute(c)).atZone(zone);

        ZonedDateTime next;
        if (Prefs.isDoneToday(c)) {
            next = todayAt.plusDays(1);
        } else if (now.isBefore(todayAt)) {
            next = todayAt;
        } else if (mode == Prefs.MODE_REPEAT) {
            next = now.plusMinutes(Prefs.intervalMinutes(c));
        } else {
            next = todayAt.plusDays(1);
        }
        setAlarm(c, next.toInstant().toEpochMilli(), pi);
    }

    static boolean isPastReminderTime(Context c) {
        return !LocalTime.now().isBefore(LocalTime.of(Prefs.hour(c), Prefs.minute(c)));
    }

    private static void setAlarm(Context c, long when, PendingIntent pi) {
        AlarmManager am = c.getSystemService(AlarmManager.class);
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
                return;
            }
        } catch (SecurityException ignored) {
            // on retombe sur une alarme non exacte
        }
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
    }

    private static PendingIntent pending(Context c, String action, int requestCode) {
        Intent i = new Intent(c, AlarmReceiver.class).setAction(action);
        return PendingIntent.getBroadcast(c, requestCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
