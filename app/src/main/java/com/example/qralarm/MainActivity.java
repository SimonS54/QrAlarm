package com.example.qralarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button setAlarmButton = findViewById(R.id.setAlarmButton);
        timePicker = findViewById(R.id.timePicker);

        timePicker.setIs24HourView(true);

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });

        ImageButton clockButton = findViewById(R.id.Clock);
        clockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToAlarmListActivity();
            }
        });
    }

    private void setAlarm() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Alarm alarm = new Alarm(hour, minute);

        saveAlarmInfo(alarm);

        setAlarmWithAlarmManager(calendar);

        Toast.makeText(this, "Alarm set for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
    }

    private void saveAlarmInfo(Alarm alarm) {
        SharedPreferences sharedPreferences = getSharedPreferences("AlarmInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        List<Alarm> alarms = getAlarms(sharedPreferences);

        alarms.add(alarm);

        Gson gson = new Gson();
        String alarmsJson = gson.toJson(alarms);

        editor.putString("alarms", alarmsJson);
        editor.apply();
    }

    private List<Alarm> getAlarms(SharedPreferences sharedPreferences) {
        String alarmsJson = sharedPreferences.getString("alarms", "[]");

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Alarm>>() {}.getType();
        return gson.fromJson(alarmsJson, listType);
    }

    private void setAlarmWithAlarmManager(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void switchToAlarmListActivity() {
        Intent intent = new Intent(this, AlarmListActivity.class);
        startActivity(intent);
    }
}
