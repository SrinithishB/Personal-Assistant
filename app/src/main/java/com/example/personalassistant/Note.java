package com.example.personalassistant;

public class Note {
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
