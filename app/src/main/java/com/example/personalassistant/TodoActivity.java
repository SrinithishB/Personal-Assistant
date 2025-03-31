package com.example.personalassistant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TodoActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;  // Ensure FirebaseAuth is declared
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_todo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.fabAddTask);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList,getApplicationContext());
        recyclerView.setAdapter(taskAdapter);

        loadTasksFromFirebase();
        PeriodicWorkRequest taskReminderWorkRequest = new PeriodicWorkRequest.Builder(TaskReminderWorker.class, 1, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(this).enqueue(taskReminderWorkRequest);
        floatingActionButton.setOnClickListener(view -> {
            if (isInternetAvailable()) {
                TaskBottomSheet taskBottomSheet = new TaskBottomSheet();
                taskBottomSheet.show(getSupportFragmentManager(), "Add task");
            } else {
                Snackbar.make(findViewById(R.id.main), getString(R.string.connect_internet), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTasksFromFirebase() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Snackbar.make(findViewById(R.id.main), "User not logged in!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance(getString(R.string.firebase_realtime_db_url)).getReference("users").child(userId).child("tasks");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(findViewById(R.id.main), "Failed to load tasks.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
