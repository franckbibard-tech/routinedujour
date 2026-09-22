package fr.routine.dujour;

import android.content.Context;

final class Routine {
    private Routine() {}

    static void setDone(Context c, boolean done) {
        Prefs.setDoneToday(c, done);
        if (done) Notifier.cancel(c);
        RoutineWidget.updateAll(c);
        Scheduler.scheduleReminder(c);
    }
}
