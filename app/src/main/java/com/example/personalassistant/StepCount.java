package com.example.personalassistant;

public class StepCount {
    private String date;
    private int steps;

    public StepCount(String date, int steps) {
        this.date = date;
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public int getSteps() {
        return steps;
    }
}
