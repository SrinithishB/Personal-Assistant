package com.example.personalassistant;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TodoActivity extends AppCompatActivity {
    private TaskAdapter taskAdapter;
    private List<String> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_todo);

        // Find Views
        EditText etTask = findViewById(R.id.etTask);
        Button btnAddTask = findViewById(R.id.btnAddTask);
        RecyclerView rvTasks = findViewById(R.id.rvTasks);

        // Setup RecyclerView
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList);
        rvTasks.setAdapter(taskAdapter);

        // Add task on button click
        btnAddTask.setOnClickListener(v -> {
            String newTask = etTask.getText().toString().trim();
            if (!newTask.isEmpty()) {
                taskAdapter.addTask(newTask);
                etTask.setText(""); // Clear input after adding
                rvTasks.smoothScrollToPosition(taskList.size() - 1); // Auto-scroll to new item
            }
        });
    }
}
