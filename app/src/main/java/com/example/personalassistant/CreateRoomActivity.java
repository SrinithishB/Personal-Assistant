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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CreateRoomActivity extends AppCompatActivity {
    EditText createRoomEditText;
    ImageView close, create;
    ProgressDialog progressDialog;
    String RoomName;
    String roomCode;
    FirebaseDatabase database;
    DatabaseReference myRef, userRoomRef;
    int imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        createRoomEditText = findViewById(R.id.RoomName);
        close = findViewById(R.id.close);
        create = findViewById(R.id.tick);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Room...");
        progressDialog.setCancelable(false);

        database = FirebaseDatabase.getInstance(getString(R.string.firebase_realtime_db_url));
        myRef = database.getReference("rooms");
        userRoomRef = database.getReference("user_rooms");

        close.setOnClickListener(v -> finish());

        create.setOnClickListener(v -> {
            RoomName = createRoomEditText.getText().toString();
            if (RoomName.isEmpty()) {
                createRoomEditText.setError("Room name is required");
            } else {
                progressDialog.show();
                createRoom();
            }
        });
    }

    private void createRoom() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Random random = new Random();
        imageId = random.nextInt(5);
        if (imageId <= 0 || imageId > 5) {
            imageId = 1;
        }
        roomCode = createRoomCode();

        checkRoomCode(roomCode, isUnique -> {
            if (!isUnique) {
                roomCode = createRoomCode();
            }

            String uniqueRoomId = myRef.push().getKey();
            RoomData roomData = new RoomData(RoomName, firebaseUser.getDisplayName(),
                    firebaseUser.getEmail(), firebaseUser.getUid(), roomCode,
                    SimpleDateFormat.getDateInstance().format(new Date()), imageId);

            if (uniqueRoomId != null) {
                myRef.child(uniqueRoomId).setValue(roomData)
                        .addOnSuccessListener(aVoid -> {
                            userRoomRef.child(firebaseUser.getUid()).child(uniqueRoomId).setValue(roomData)
                                    .addOnSuccessListener(aVoid2 -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(CreateRoomActivity.this, "Room Created Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(CreateRoomActivity.this, "Failed to Add Room to User", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(CreateRoomActivity.this, "Failed to Create Room", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void checkRoomCode(String roomCode, OnCheckCompleteListener listener) {
        myRef.orderByChild("roomCode").equalTo(roomCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.onCheckComplete(snapshot.getChildrenCount() == 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(CreateRoomActivity.this, "Error checking room code", Toast.LENGTH_SHORT).show();
                listener.onCheckComplete(false);
            }
        });
    }

    private interface OnCheckCompleteListener {
        void onCheckComplete(boolean isUnique);
    }

    private String createRoomCode() {
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * CHARS.length());
            salt.append(CHARS.charAt(index));
        }
        return salt.toString();
    }
}