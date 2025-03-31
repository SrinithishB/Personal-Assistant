package com.example.personalassistant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    private RecyclerView notesRecyclerView;
    private NotesAdapter noteAdapter;
    private List<Note> noteList;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        auth = FirebaseAuth.getInstance();
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        FloatingActionButton addNoteFab = findViewById(R.id.addNoteFab);

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteList = new ArrayList<>();
        noteAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(noteAdapter);

        loadNotesFromFirebase();

        addNoteFab.setOnClickListener(v -> {
            startActivity(new Intent(NotesActivity.this, AddEditNoteActivity.class));
        });
    }

    private void loadNotesFromFirebase() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance(getString(R.string.firebase_realtime_db_url)).getReference("users").child(userId).child("notes");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noteList.clear();
                for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    noteList.add(note);
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotesActivity.this, "Failed to load notes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
