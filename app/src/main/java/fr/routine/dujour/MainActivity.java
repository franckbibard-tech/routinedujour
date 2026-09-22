package fr.routine.dujour;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView status;
    private Button btnDone, btnUndo, btnTime;
    private RadioGroup modeGroup, intervalGroup;
    private View repeatBox;
    private boolean syncing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        btnDone = findViewById(R.id.btn_done);
        btnUndo = findViewById(R.id.btn_undo);
        btnTime = findViewById(R.id.btn_time);
        modeGroup = findViewById(R.id.mode_group);
        intervalGroup = findViewById(R.id.interval_group);
        repeatBox = findViewById(R.id.repeat_box);

        Notifier.ensureChannel(this);

        btnDone.setOnClickListener(v -> { Routine.setDone(this, true); refresh(); });
        btnUndo.setOnClickListener(v -> { Routine.setDone(this, false); refresh(); });

        modeGroup.setOnCheckedChangeListener((g, id) -> {
            if (syncing) return;
            int mode = id == R.id.mode_fixed ? Prefs.MODE_FIXED
                    : id == R.id.mode_repeat ? Prefs.MODE_REPEAT
                    : Prefs.MODE_WIDGET;
            Prefs.setMode(this, mode);
            if (mode != Prefs.MODE_WIDGET) askNotificationPermission();
            Scheduler.scheduleReminder(this);
            refresh();
        });

        intervalGroup.setOnCheckedChangeListener((g, id) -> {
            if (syncing) return;
            int minutes = id == R.id.every_15 ? 15 : id == R.id.every_60 ? 60 : 30;
            Prefs.setInterval(this, minutes);
            Scheduler.scheduleReminder(this);
        });

        btnTime.setOnClickListener(v -> new TimePickerDialog(this, (tp, h, m) -> {
            Prefs.setTime(this, h, m);
            Scheduler.scheduleReminder(this);
            refresh();
        }, Prefs.hour(this), Prefs.minute(this), true).show());

        if (Prefs.mode(this) != Prefs.MODE_WIDGET) askNotificationPermission();
        Scheduler.scheduleAll(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RoutineWidget.updateAll(this);
        refresh();
    }

    private void refresh() {
        boolean done = Prefs.isDoneToday(this);
        status.setText(done ? R.string.msg_done : R.string.msg_todo);
        status.setBackgroundResource(done ? R.drawable.bg_done : R.drawable.bg_todo);
        btnDone.setVisibility(done ? View.GONE : View.VISIBLE);
        btnUndo.setVisibility(done ? View.VISIBLE : View.GONE);

        int mode = Prefs.mode(this);
        int interval = Prefs.intervalMinutes(this);
        syncing = true;
        modeGroup.check(mode == Prefs.MODE_FIXED ? R.id.mode_fixed
                : mode == Prefs.MODE_REPEAT ? R.id.mode_repeat
                : R.id.mode_widget);
        intervalGroup.check(interval == 15 ? R.id.every_15
                : interval == 60 ? R.id.every_60
                : R.id.every_30);
        syncing = false;

        btnTime.setText(getString(R.string.reminder_time, Prefs.hour(this), Prefs.minute(this)));
        btnTime.setVisibility(mode == Prefs.MODE_WIDGET ? View.GONE : View.VISIBLE);
        repeatBox.setVisibility(mode == Prefs.MODE_REPEAT ? View.VISIBLE : View.GONE);
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }
}
