package com.example.personalassistant;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class TaskBottomSheet extends BottomSheetDialogFragment {
    private TextInputEditText inputTask;
    private TextView selectedDateText, selectedTimeText;
    private Button addTaskButton;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private String selectedDate = "";
    private String selectedTime = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_task, container, false);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Initialize Views
        inputTask = view.findViewById(R.id.inputTask);
        selectedDateText = view.findViewById(R.id.selectedDateText);
        selectedTimeText = view.findViewById(R.id.selectedTimeText);
        addTaskButton = view.findViewById(R.id.addTaskButton);

        // Set Click Listeners
        selectedDateText.setOnClickListener(v -> showDatePicker());
        selectedTimeText.setOnClickListener(v -> showTimePicker());
        addTaskButton.setOnClickListener(v -> addTaskToFirebase());

        return view;
    }

    private void addTaskToFirebase() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user ID and reference to Firebase
        String userId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance(getString(R.string.firebase_realtime_db_url)).getReference("users").child(userId).child("tasks");

        // Get task details
        String taskName = inputTask.getText().toString().trim();
        if (taskName.isEmpty()) {
            inputTask.setError("Please enter a task");
            return;
        }

        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(getContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Task Object
        String taskId = databaseReference.push().getKey();
        Task task = new Task(taskId, taskName, selectedDate, selectedTime, "Pending");

        // Upload to Firebase
        databaseReference.child(taskId).setValue(task)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Task Added Successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, day1) -> {
            selectedDate = day1 + "/" + (month1 + 1) + "/" + year1;
            selectedDateText.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
            selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
            selectedTimeText.setText(selectedTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }
}
