package com.example.personalassistant;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhysicalActivity extends AppCompatActivity implements SensorEventListener {

    private static final int SENSOR_PERMISSION_CODE = 1;
    private SensorManager sensorManager;
    private boolean running = false;
    private float totalSteps = 0f;
    private float previousTotalSteps = 0f;
    private int stepGoal = 10000; // Default step goal
    private TextView tvStepsTaken, tvCaloriesBurned, distanceText, activeTimeText, goalText;
    private Button changeGoalBtn;
    private ProgressBar progressBar;
    private static final float CALORIES_PER_STEP = 0.04f;
    private static final float STEP_LENGTH = 0.78f; // in meters
    private static final float STEP_TIME_SECONDS = 0.5f; // in seconds
    private LinearLayout linearLayout;
    private StepCountDBHandler dbHandler;
    private String currentDate;
    private String lastRecordedDate;
    private static final int FOREGROUND_SERVICE_PERMISSION_CODE = 2;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);

        tvStepsTaken = findViewById(R.id.tv_stepsTaken);
        tvCaloriesBurned = findViewById(R.id.caloriesText);
        distanceText = findViewById(R.id.distanceText);
        activeTimeText = findViewById(R.id.activeTimeText);
        changeGoalBtn = findViewById(R.id.changeGoalBtn);
        goalText = findViewById(R.id.goalText);
        progressBar = findViewById(R.id.progress_bar);
        linearLayout = findViewById(R.id.linearLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_top_slide);
        linearLayout.startAnimation(animation);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        dbHandler = new StepCountDBHandler(this);
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, SENSOR_PERMISSION_CODE);
            } else {
                Toast.makeText(this, "Android Version not supported", Toast.LENGTH_SHORT).show();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, FOREGROUND_SERVICE_PERMISSION_CODE);
            }
        }

        // Start the service if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED) {
            startStepCounterService();
        }

        createNotificationChannel(); // Create notification channel
        loadDataFromDB();
        loadGoal();
        resetSteps();
        loadStepData();

        changeGoalBtn.setOnClickListener(view -> showGoalPicker());
    }
    private void loadStepData() {
        List<StepCount> stepList = dbHandler.getAllSteps();
        StepCountAdapter adapter = new StepCountAdapter(stepList);
        recyclerView.setAdapter(adapter);
    }

    private void startStepCounterService() {
        Intent serviceIntent = new Intent(this, StepCounterService.class);
        startService(serviceIntent);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "step_goal_channel";
            String channelName = "Step Goal Notifications";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showGoalPicker() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(PhysicalActivity.this);
        View view = LayoutInflater.from(PhysicalActivity.this).inflate(R.layout.bottom_sheet_step_goal, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        Button cancel = view.findViewById(R.id.cancel_button);
        Button goal = view.findViewById(R.id.change);
        NumberPicker numberPicker = view.findViewById(R.id.numberPicker);

        int minValue = 1000, maxValue = 100000, step = 1000;
        int totalItems = (maxValue - minValue) / step + 1;
        String[] numberValues = new String[totalItems];

        for (int i = 0; i < totalItems; i++) {
            numberValues[i] = String.valueOf(minValue + (i * step));
        }

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(numberValues.length - 1);
        numberPicker.setDisplayedValues(numberValues);
        numberPicker.setWrapSelectorWheel(false);

        goal.setOnClickListener(view1 -> {
            int selectedIndex = numberPicker.getValue();
            stepGoal = Integer.parseInt(numberValues[selectedIndex]);
            saveGoal();
            goalText.setText(String.format("%d", stepGoal));
            Toast.makeText(getApplicationContext(), "Goal Set: " + stepGoal, Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        cancel.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
    }



    private void loadDataFromDB() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        lastRecordedDate = getSharedPreferences("step_data", Context.MODE_PRIVATE).getString("lastRecordedDate", "");

        // Check if the date has changed
        if (!today.equals(lastRecordedDate)) {
            // Reset steps for the new day
            totalSteps = 0; // Reset total steps
            dbHandler.insertOrUpdateStepCount(today, 0); // Reset in DB
            SharedPreferences.Editor editor = getSharedPreferences("step_data", Context.MODE_PRIVATE).edit();
            editor.putString("lastRecordedDate", today);
            editor.apply();
        }

        int steps = dbHandler.getStepCount(today);
        totalSteps += steps; // Add steps from the database to totalSteps
        tvStepsTaken.setText(String.valueOf(totalSteps));
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepSensor == null) {
            Toast.makeText(this, "No step sensor detected", Toast.LENGTH_SHORT).show();
        } else {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED) {
                startStepCounterService();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running && event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            int currentSteps = (int) event.values[0]; // Step detected (usually 1 per event)

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


            // Retrieve existing steps for today before adding new ones
            int previousSteps = dbHandler.getStepCount(today);
            totalSteps = previousSteps + currentSteps; // Ensure steps are added correctly

            // Update UI
            tvStepsTaken.setText(String.valueOf(totalSteps));

            // Save updated step count in the database
            dbHandler.insertOrUpdateStepCount(today, (int) totalSteps);
            Log.d("DB_CHECK", "Steps: " + dbHandler.getAllSteps().toString());


            // Calculate progress
            int progress = (int) (((float) totalSteps / stepGoal) * 100);
            progressBar.setProgress(progress);

            // Calculate and update stats
            float caloriesBurned = totalSteps * CALORIES_PER_STEP;
            float distanceCovered = (totalSteps * STEP_LENGTH) / 1000; // km
            float activeTimeMinutes = (totalSteps * STEP_TIME_SECONDS) / 60; // minutes

            tvCaloriesBurned.setText(String.format("%.2f kcal", caloriesBurned));
            distanceText.setText(String.format("%.2f km", distanceCovered));
            activeTimeText.setText(String.format("%.1f min", activeTimeMinutes));

            // Trigger notification if goal is reached
            if (progress >= 100) {
                sendGoalReachedNotification();
            }
        }
    }


    private void resetSteps() {
        tvStepsTaken.setOnClickListener(v ->
                Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        );

        tvStepsTaken.setOnLongClickListener(v -> {
            totalSteps = 0; // Reset total steps
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            dbHandler.insertOrUpdateStepCount(today, 0); // Reset in DB
            tvStepsTaken.setText("0");
            tvCaloriesBurned.setText("0.00 kcal");
            distanceText.setText("0.00 km");
            activeTimeText.setText("0.0 min");
            progressBar.setProgress(0); // Reset progress bar
            return true;
        });
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}



    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        sensorManager.unregisterListener(this);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED) {
//            Intent serviceIntent = new Intent(this, StepCounterService.class);
//            stopService(serviceIntent);
//        }

    }
    private void saveGoal() {
        SharedPreferences sharedPreferences = getSharedPreferences("goal", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("goal", stepGoal);
        editor.apply();
    }

    private void loadGoal() {
        SharedPreferences sharedPreferences = getSharedPreferences("goal", Context.MODE_PRIVATE);
        stepGoal = sharedPreferences.getInt("goal", 10000);
        goalText.setText(String.format("%d", stepGoal));
    }

    private void sendGoalReachedNotification() {
        String channelId = "step_goal_channel";

        Intent intent = new Intent(this, PhysicalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.walk)
                .setContentTitle("Congratulations!")
                .setContentText("You've reached your step goal of " + stepGoal + " steps!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}