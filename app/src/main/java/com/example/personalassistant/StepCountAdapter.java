package com.example.personalassistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, stepsTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            stepsTextView = itemView.findViewById(R.id.stepsTextView);
        }
    }
}