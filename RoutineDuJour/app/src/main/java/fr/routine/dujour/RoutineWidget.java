package fr.routine.dujour;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class RoutineWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context c, AppWidgetManager manager, int[] ids) {
        updateAll(c);
    }

    @Override
    public void onEnabled(Context c) {
        Scheduler.scheduleAll(c);
    }

    static void updateAll(Context c) {
        AppWidgetManager manager = AppWidgetManager.getInstance(c);
        int[] ids = manager.getAppWidgetIds(new ComponentName(c, RoutineWidget.class));
        if (ids == null || ids.length == 0) return;

        boolean done = Prefs.isDoneToday(c);
        RemoteViews views = new RemoteViews(c.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.widget_text,
                c.getString(done ? R.string.msg_done : R.string.msg_todo));
        views.setInt(R.id.widget_root, "setBackgroundResource",
                done ? R.drawable.bg_done : R.drawable.bg_todo);

        Intent open = new Intent(c, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        views.setOnClickPendingIntent(R.id.widget_root, PendingIntent.getActivity(c, 0, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));

        manager.updateAppWidget(ids, views);
    }
}
