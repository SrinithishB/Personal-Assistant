package com.example.personalassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.getTaskName());
//        holder.taskDateTime.setText(task.getDate() + " - " + task.getTime());

        // Set checkbox status from Firebase
        holder.taskCheckbox.setChecked(task.getStatus().equals("Completed"));

        // Handle checkbox click event
        holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateTaskStatus(task, isChecked);
        });
    }

    private void updateTaskStatus(Task task, boolean isCompleted) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get Firebase reference
        String userId = currentUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://personal-assistant-89c5a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")
                .child(userId)
                .child("tasks")
                .child(task.getTaskId());

        // Update status in Firebase
        String newStatus = isCompleted ? "Completed" : "Pending";
        databaseReference.child("status").setValue(newStatus)
                .addOnSuccessListener(unused -> Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDateTime;
        CheckBox taskCheckbox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
//            taskDateTime = itemView.findViewById(R.id.taskDateTime);
            taskCheckbox = itemView.findViewById(R.id.taskCheckbox);
        }
    }
}
