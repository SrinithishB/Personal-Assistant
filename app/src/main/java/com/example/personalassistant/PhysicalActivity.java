package com.example.personalassistant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PhysicalActivity extends AppCompatActivity implements SensorEventListener {

    private static final int SENSOR_PERMISSION_CODE = 1;
    private SensorManager sensorManager;
    private boolean running = false;
    private float totalSteps = 0f;
    private float previousTotalSteps = 0f;
    private int stepGoal = 10000; // Default step goal
    private TextView tvStepsTaken, tvCaloriesBurned, distanceText, activeTimeText,goalText;
    private Button changeGoalBtn;

    private ProgressBar progressBar;
    private static final float CALORIES_PER_STEP = 0.04f;
    private static final float STEP_LENGTH = 0.78f;
    private static final float STEP_TIME_SECONDS = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);

        // Initialize UI elements
        tvStepsTaken = findViewById(R.id.tv_stepsTaken);
        tvCaloriesBurned = findViewById(R.id.caloriesText);
        distanceText = findViewById(R.id.distanceText);
        activeTimeText = findViewById(R.id.activeTimeText);
        changeGoalBtn = findViewById(R.id.changeGoalBtn);
        goalText=findViewById(R.id.goalText);
        progressBar=findViewById(R.id.progress_bar);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Request permission if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACTIVITY_RECOGNITION}, SENSOR_PERMISSION_CODE);
        } else {
            loadData();
        }

//        resetSteps();
        resetStepsIfNewDay();
        loadData();
        loadGoal();

        changeGoalBtn.setOnClickListener(view -> showGoalPicker());
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

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            running = true;
            Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepSensor == null) {
                Toast.makeText(this, "No step sensor detected", Toast.LENGTH_SHORT).show();
            } else {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            totalSteps = event.values[0];
            int currentSteps = (int) (totalSteps - previousTotalSteps);

            // Calculate percentage progress
            int progress = (int) (((float) currentSteps / stepGoal) * 100);
            progressBar.setProgress(progress);

            float caloriesBurned = currentSteps * CALORIES_PER_STEP;
            float distanceCovered = (currentSteps * STEP_LENGTH) / 1000; // km
            float activeTimeMinutes = (currentSteps * STEP_TIME_SECONDS) / 60; // minutes

            tvStepsTaken.setText(String.valueOf(currentSteps));
            tvCaloriesBurned.setText(String.format("%.2f kcal", caloriesBurned));
            distanceText.setText(String.format("%.2f km", distanceCovered));
            activeTimeText.setText(String.format("%.1f min", activeTimeMinutes));

            saveData();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void resetSteps() {
        tvStepsTaken.setOnClickListener(v ->
                Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        );

        tvStepsTaken.setOnLongClickListener(v -> {
            previousTotalSteps = totalSteps;
            tvStepsTaken.setText("0");
            tvCaloriesBurned.setText("0.00 kcal");
            distanceText.setText("0.00 km");
            activeTimeText.setText("0.0 min");
            saveData();
            return true;
        });
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("stepCount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("previousTotalSteps", previousTotalSteps);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("stepCount", Context.MODE_PRIVATE);
        previousTotalSteps = sharedPreferences.getFloat("previousTotalSteps", 0f);

        int currentSteps = (int) (totalSteps - previousTotalSteps);

        // Restore progress bar
        int progress = (int) (((float) currentSteps / stepGoal) * 100);
        progressBar.setProgress(progress);

        tvStepsTaken.setText(String.valueOf(currentSteps));
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
        // Update the goalText view
        goalText.setText(String.format("%d", stepGoal));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SENSOR_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadData();
            } else {
                Toast.makeText(this, "Permission denied! App may not work correctly.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void resetStepsIfNewDay() {
        SharedPreferences sharedPreferences = getSharedPreferences("stepCount", Context.MODE_PRIVATE);
        long lastResetTime = sharedPreferences.getLong("lastResetTime", 0);
        long currentTime = System.currentTimeMillis();

        // Check if it's a new day
        if (lastResetTime == 0 || isNewDay(lastResetTime, currentTime)) {
            previousTotalSteps = 0;
            saveData(); // Reset the step data
            saveLastResetTime(currentTime); // Save the current date as the last reset time
        }
    }

    private boolean isNewDay(long lastResetTime, long currentTime) {
        // Convert both times to days
        return currentTime / (1000 * 60 * 60 * 24) != lastResetTime / (1000 * 60 * 60 * 24);
    }

    private void saveLastResetTime(long currentTime) {
        SharedPreferences sharedPreferences = getSharedPreferences("stepCount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lastResetTime", currentTime);
        editor.apply();
    }

}
