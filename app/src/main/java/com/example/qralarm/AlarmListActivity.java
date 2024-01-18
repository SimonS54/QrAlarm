package com.example.qralarm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class AlarmListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateBackToMainActivity();
            }
        });

        displayAlarms();
    }

    private void displayAlarms() {
        SharedPreferences sharedPreferences = getSharedPreferences("AlarmInfo", MODE_PRIVATE);

        LinearLayout alarmContainer = findViewById(R.id.alarmContainer);
        alarmContainer.removeAllViews();

        List<Alarm> alarms = getAlarms(sharedPreferences);

        for (Alarm alarm : alarms) {
            int hour = alarm.getHour();
            int minute = alarm.getMinute();

            Log.d("AlarmListActivity", "Retrieved alarm with hour: " + hour + ", minute: " + minute);

            LinearLayout alarmLayout = new LinearLayout(this);
            alarmLayout.setOrientation(LinearLayout.HORIZONTAL);
            alarmLayout.setGravity(Gravity.CENTER);

            TextView alarmTextView = new TextView(this);
            alarmTextView.setText(String.format("Alarm at %02d:%02d", hour, minute));
            alarmTextView.setTextSize(30);
            alarmTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            ImageButton deleteButton = new ImageButton(this);
            deleteButton.setImageResource(R.drawable.trash);
            deleteButton.setBackground(null);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAlarm(alarm, sharedPreferences);
                    displayAlarms();
                }
            });

            alarmLayout.addView(alarmTextView);
            alarmLayout.addView(deleteButton);

            alarmContainer.addView(alarmLayout);
        }
    }


    private void deleteAlarm(Alarm alarm, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        List<Alarm> alarms = getAlarms(sharedPreferences);

        for (int i = 0; i < alarms.size(); i++) {
            if (alarms.get(i).getHour() == alarm.getHour() && alarms.get(i).getMinute() == alarm.getMinute()) {
                alarms.remove(i);
                break;
            }
        }

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

    private void navigateBackToMainActivity() {
        finish();
    }
}