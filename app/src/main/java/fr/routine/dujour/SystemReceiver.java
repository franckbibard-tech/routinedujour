package fr.routine.dujour;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Redémarrage, mise à jour de l'appli, changement d'heure/date : on remet tout d'aplomb. */
public class SystemReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent intent) {
        RoutineWidget.updateAll(c);
        Scheduler.scheduleAll(c);
    }
}
