package com.example.personalassistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StepCountAdapter extends RecyclerView.Adapter<StepCountAdapter.ViewHolder> {
    private List<StepCount> stepList;
    private static final float CALORIES_PER_STEP = 0.04f;
    private static final float STEP_LENGTH = 0.78f; // in meters
    private static final float STEP_TIME_SECONDS = 0.5f; // in seconds
    public StepCountAdapter(List<StepCount> stepList) {
        this.stepList = stepList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step_count, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StepCount step = stepList.get(position);

        // Format the date properly
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()); // Example: 19 Feb 2025

        try {
            Date date = inputFormat.parse(step.getDate()); // Parse the stored date
            holder.dateTextView.setText(outputFormat.format(date)); // Convert to readable format
        } catch (ParseException e) {
            holder.dateTextView.setText(step.getDate()); // Fallback in case of error
        }

        holder.stepsTextView.setText(String.valueOf(step.getSteps()));
        float caloriesBurned = step.getSteps() * CALORIES_PER_STEP;
        float distanceCovered = (step.getSteps() * STEP_LENGTH) / 1000; // km
        float activeTimeMinutes = (step.getSteps() * STEP_TIME_SECONDS) / 60; // minutes

        // Example: Assuming StepCount model has these values
        holder.caloriesText.setText(String.format("%.2f kcal", caloriesBurned));
        holder.distanceText.setText(String.format("%.2f km", distanceCovered));
        holder.activeTimeText.setText(String.format("%.1f min", activeTimeMinutes));
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, stepsTextView, caloriesText, distanceText, activeTimeText;
        ImageView caloriesIcon, distanceIcon, activeTimeIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            stepsTextView = itemView.findViewById(R.id.stepsTextView);
            caloriesText = itemView.findViewById(R.id.caloriesText);
            distanceText = itemView.findViewById(R.id.distanceText);
            activeTimeText = itemView.findViewById(R.id.activeTimeText);

        }
    }
}
