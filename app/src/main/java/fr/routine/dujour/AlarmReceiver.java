package fr.routine.dujour;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    static final String ACTION_REMINDER = "fr.routine.dujour.REMINDER";
    static final String ACTION_MIDNIGHT = "fr.routine.dujour.MIDNIGHT";
    static final String ACTION_DONE = "fr.routine.dujour.DONE";

    @Override
    public void onReceive(Context c, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            case ACTION_REMINDER:
                if (Prefs.mode(c) != Prefs.MODE_WIDGET
                        && !Prefs.isDoneToday(c)
                        && Scheduler.isPastReminderTime(c)) {
                    Notifier.show(c);
                }
                Scheduler.scheduleReminder(c);
                break;
            case ACTION_MIDNIGHT:
                Notifier.cancel(c);
                RoutineWidget.updateAll(c);
                Scheduler.scheduleAll(c);
                break;
            case ACTION_DONE:
                Routine.setDone(c, true);
                break;
            default:
                break;
        }
    }
}
