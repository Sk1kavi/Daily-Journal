package com.example.dailyjournal;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        setupNotificationSettings(view);
        setupThemeSettings(view);

        return view;
    }

    private void setupNotificationSettings(View view) {
        SwitchCompat notificationSwitch = view.findViewById(R.id.switchNotifications);
        EditText etReminderTime = view.findViewById(R.id.etReminderTime);

        // Load saved settings
        notificationSwitch.setChecked(prefs.getBoolean("notifications_enabled", true));
        etReminderTime.setText(prefs.getString("reminder_time", "20:00"));

        // Save settings when changed
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            updateNotificationSchedule();
        });

        etReminderTime.setOnEditorActionListener((v, actionId, event) -> {
            prefs.edit().putString("reminder_time", v.getText().toString()).apply();
            updateNotificationSchedule();
            return false;
        });
    }

    private void setupThemeSettings(View view) {
        SwitchCompat darkModeSwitch = view.findViewById(R.id.switchDarkMode);

        // Set initial state (false = light, true = dark)
        boolean isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        darkModeSwitch.setChecked(isDarkMode);

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Just these 2 lines do all the work!
            AppCompatDelegate.setDefaultNightMode(isChecked ?
                    AppCompatDelegate.MODE_NIGHT_YES :
                    AppCompatDelegate.MODE_NIGHT_NO);
            requireActivity().recreate();
        });
    }

    private void updateNotificationSchedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                // Launch intent to request permission
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }
        boolean enabled = prefs.getBoolean("notifications_enabled", true);
        String time = prefs.getString("reminder_time", "20:00");

        if (enabled) {
            // Parse time and schedule notifications
            ReminderScheduler.scheduleDailyReminder(getContext(), time);
        } else {
            ReminderScheduler.cancelReminder(getContext());
        }
    }
}
