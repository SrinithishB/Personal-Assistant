package com.example.personalassistant;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinRoomActivity extends AppCompatActivity {
    EditText roomCodeEditText;
    ImageView close, create;
    ProgressDialog progressDialog;
    String roomCode;
    FirebaseDatabase database;
    DatabaseReference myRef, userRoomRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        roomCodeEditText = findViewById(R.id.RoomCode);
        close = findViewById(R.id.close);
        create = findViewById(R.id.tick);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Joining Room...");
        progressDialog.setCancelable(false);

        database = FirebaseDatabase.getInstance(getString(R.string.firebase_realtime_db_url));
        myRef = database.getReference("rooms");
        userRoomRef = database.getReference("user_rooms");

        close.setOnClickListener(v -> finish());

        create.setOnClickListener(v -> {
            roomCode = roomCodeEditText.getText().toString().trim();
            if (roomCode.isEmpty()) {
                roomCodeEditText.setError("Room code is required");
            } else {
                progressDialog.show();
                joinRoom();
            }
        });
    }

    private void joinRoom() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        myRef.orderByChild("room_code").equalTo(roomCode.trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (!snapshot.exists()) {
                    Toast.makeText(JoinRoomActivity.this, "Room Not found", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        RoomData roomData = roomSnapshot.getValue(RoomData.class);
                        if (roomData != null) {
                            userRoomRef.child(firebaseUser.getUid()).child(roomSnapshot.getKey()).setValue(roomData)
                                    .addOnSuccessListener(aVoid ->{
                                        Toast.makeText(JoinRoomActivity.this, "Room Joined Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(JoinRoomActivity.this, "Failed to Join Room", Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(JoinRoomActivity.this, "Error checking room code", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
