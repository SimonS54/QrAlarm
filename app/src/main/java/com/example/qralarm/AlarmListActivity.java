package com.example.qralarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

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

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith("alarm_")) {
                int hour = sharedPreferences.getInt(entry.getKey() + "_hour", 0);
                int minute = sharedPreferences.getInt(entry.getKey() + "_minute", 0);

                TextView alarmTextView = new TextView(this);
                alarmTextView.setText("Alarm at " + hour + ":" + minute);
                alarmTextView.setTextSize(18);
                alarmTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white)); // Set text color

                alarmContainer.addView(alarmTextView);
            }
        }
    }

    private void navigateBackToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
