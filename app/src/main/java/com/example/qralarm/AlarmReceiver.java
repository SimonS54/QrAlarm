package com.example.qralarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (hasPermission(context, android.Manifest.permission.VIBRATE)) {
            playDefaultAlarmSound(context);
            startVibration(context);
        } else {
            Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();
        }

        Intent qrScannerIntent = new Intent(context, QRScannerActivity.class);
        qrScannerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(qrScannerIntent);
    }

    private boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    private void playDefaultAlarmSound(Context context) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmSound);
        ringtone.play();
    }

    private void startVibration(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect vibrationEffect = VibrationEffect.createWaveform(
                        new long[]{0, 1000, 1000}, 0);
                vibrator.vibrate(vibrationEffect);
            } else {
                vibrator.vibrate(new long[]{0, 1000, 1000}, 0);
            }
        }
    }
}
