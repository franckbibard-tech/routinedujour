package fr.routine.dujour;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Build;

final class Notifier {
    private static final String CHANNEL = "rappel_routine";
    private static final int ID = 1;

    private Notifier() {}

    static void ensureChannel(Context c) {
        NotificationChannel ch = new NotificationChannel(CHANNEL,
                c.getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH);
        c.getSystemService(NotificationManager.class).createNotificationChannel(ch);
    }

    static void show(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ensureChannel(c);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        Intent openIntent = new Intent(c, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent open = PendingIntent.getActivity(c, 10, openIntent, flags);
        PendingIntent done = PendingIntent.getBroadcast(c, 11,
                new Intent(c, AlarmReceiver.class).setAction(AlarmReceiver.ACTION_DONE), flags);

        Notification.Action doneAction = new Notification.Action.Builder(
                Icon.createWithResource(c, R.drawable.ic_notif),
                c.getString(R.string.notif_action_done), done).build();

        Notification n = new Notification.Builder(c, CHANNEL)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(c.getString(R.string.notif_title))
                .setContentText(c.getString(R.string.msg_todo))
                .setCategory(Notification.CATEGORY_REMINDER)
                .setContentIntent(open)
                .setAutoCancel(true)
                .addAction(doneAction)
                .build();

        c.getSystemService(NotificationManager.class).notify(ID, n);
    }

    static void cancel(Context c) {
        c.getSystemService(NotificationManager.class).cancel(ID);
    }
}
