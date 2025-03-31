package com.example.personalassistant;

public class Task {
    private String taskId;
    private String taskName;
    private String date;
    private String time;
    private String status; // New Field: "Pending" or "Completed"

    public Task() {
        // Required for Firebase
    }

    public Task(String taskId, String taskName, String date, String time, String status) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static class Note {
        private String id;
        private String title;
        private String content;
        private String timestamp;

        public Note() {
            // Required for Firebase
        }

        public Note(String id, String title, String content, String timestamp) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getTimestamp() { return timestamp; }
    }
}

