package com.example.personalassistant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepCounterService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private boolean running = false;
    private float totalSteps = 0f;
    private StepCountDBHandler dbHandler;
    private static final String CHANNEL_ID = "StepCounterServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, getNotification());
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            running = true;
        }
        dbHandler = new StepCountDBHandler(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Step Counter Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, PhysicalActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Step Counter")
                .setContentText("Counting steps...")
                .setSmallIcon(R.drawable.walk)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running && event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            int currentSteps = (int) event.values[0];
            totalSteps += currentSteps;
            // Here you can save the totalSteps to your database or shared preferences
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            dbHandler.insertOrUpdateStepCount(today, (int) totalSteps);
//            checkGoalReached();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (running) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
//    private void checkGoalReached() {
//        SharedPreferences prefs = getSharedPreferences("goal", MODE_PRIVATE);
//        boolean goalReached = prefs.getBoolean("goal_reached", false);
//        int stepGoal=prefs.getInt("goal",MODE_PRIVATE);
//
//        if (totalSteps >= stepGoal && !goalReached) {
//            sendGoalReachedNotification(stepGoal);
//            // Mark the goal as reached for today
//            prefs.edit().putBoolean("goal_reached", true).apply();
//        } else if (totalSteps < stepGoal) {
//            // Reset the goal reached status if the goal is not met
//            prefs.edit().putBoolean("goal_reached", false).apply();
//        }
//    }

//    private void sendGoalReachedNotification(int stepGoal) {
//        String channelId = CHANNEL_ID;
//
//        Intent intent = new Intent(this, PhysicalActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.walk)
//                .setContentTitle("Congratulations!")
//                .setContentText("You've reached your step goal of " + stepGoal + " steps!")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(1, builder.build());
//    }

}