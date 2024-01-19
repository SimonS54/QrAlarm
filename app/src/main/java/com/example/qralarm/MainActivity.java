package com.example.qralarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        setButtonClickListeners();
    }

    private void initializeUI() {
        Button setAlarmButton = findViewById(R.id.setAlarmButton);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
    }

    private void setButtonClickListeners() {
        findViewById(R.id.setAlarmButton).setOnClickListener(v -> setAlarm());
        findViewById(R.id.Clock).setOnClickListener(v -> switchToAlarmListActivity());
        findViewById(R.id.download).setOnClickListener(v -> startDownload());
        findViewById(R.id.qrcode).setOnClickListener(v -> startQRScanner());
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

        showSetAlarmNotification();

        Toast.makeText(this, "Alarm set for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
    }

    private void showSetAlarmNotification() {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "alarm_channel")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Alarm Set")
                .setContentText("Your alarm is set for the specified time.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "alarm_channel",
                    "Alarm Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for alarm notifications");
            channel.enableLights(true);
            channel.setLightColor(android.graphics.Color.RED);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
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

    private void startQRScanner() {
        Intent intent = new Intent(this, QRScannerActivity.class);
        startActivity(intent);
    }

    private void startDownload() {
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.frame);

        Bitmap bitmap = drawable.getBitmap();

        File directory = new File(Environment.getExternalStorageDirectory() + "/Images");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "frame.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Downloaded to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }
}
