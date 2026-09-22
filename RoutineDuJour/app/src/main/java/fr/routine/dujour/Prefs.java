package fr.routine.dujour;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;

/** Stockage : date du jour validé + réglages des rappels. */
final class Prefs {
    static final int MODE_WIDGET = 0;
    static final int MODE_FIXED = 1;
    static final int MODE_REPEAT = 2;

    private static final String FILE = "routine";
    private static final String KEY_DONE_DATE = "done_date";
    private static final String KEY_MODE = "mode";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_MINUTE = "minute";
    private static final String KEY_INTERVAL = "interval";

    private Prefs() {}

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    static boolean isDoneToday(Context c) {
        return LocalDate.now().toString().equals(sp(c).getString(KEY_DONE_DATE, ""));
    }

    static void setDoneToday(Context c, boolean done) {
        sp(c).edit().putString(KEY_DONE_DATE, done ? LocalDate.now().toString() : "").commit();
    }

    static int mode(Context c) { return sp(c).getInt(KEY_MODE, MODE_WIDGET); }
    static void setMode(Context c, int mode) { sp(c).edit().putInt(KEY_MODE, mode).commit(); }

    static int hour(Context c) { return sp(c).getInt(KEY_HOUR, 20); }
    static int minute(Context c) { return sp(c).getInt(KEY_MINUTE, 0); }
    static void setTime(Context c, int h, int m) {
        sp(c).edit().putInt(KEY_HOUR, h).putInt(KEY_MINUTE, m).commit();
    }

    static int intervalMinutes(Context c) { return sp(c).getInt(KEY_INTERVAL, 30); }
    static void setInterval(Context c, int minutes) { sp(c).edit().putInt(KEY_INTERVAL, minutes).commit(); }
}
