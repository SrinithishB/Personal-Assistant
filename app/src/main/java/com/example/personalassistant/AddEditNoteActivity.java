package com.example.personalassistant;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditNoteActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextContent;
    private Button saveNoteButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String noteId = null; // Will be used when editing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        saveNoteButton = findViewById(R.id.saveNoteButton);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance(getString(R.string.firebase_realtime_db_url)).getReference("users").child(userId).child("notes");

        // Check if we're editing an existing note
        if (getIntent().hasExtra("noteId")) {
            noteId = getIntent().getStringExtra("noteId");
            editTextTitle.setText(getIntent().getStringExtra("title"));
            editTextContent.setText(getIntent().getStringExtra("content"));
        }

        saveNoteButton.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        if (noteId == null) { // Creating a new note
            noteId = databaseReference.push().getKey();
        }

        Note note = new Note(noteId, title, content, timestamp);
        databaseReference.child(noteId).setValue(note)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddEditNoteActivity.this, "Note saved!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to NotesActivity
                })
                .addOnFailureListener(e -> Toast.makeText(AddEditNoteActivity.this, "Failed to save note", Toast.LENGTH_SHORT).show());
    }
}
