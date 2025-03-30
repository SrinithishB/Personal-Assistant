package com.example.personalassistant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends AppCompatActivity {
    TextView roomName;
    String roomCode,roomId;
    private FirebaseAuth mAuth;
    Intent intent;
    DatabaseReference roomRef, userRoomRef;
    FirebaseDatabase database;
    RoomData roomData;
    private ProgressBar progressBar;
    private FirebaseStorage storage;
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<FileModel> fileList;
    private static final int REQUEST_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressBar=findViewById(R.id.progressBar);
        roomName=findViewById(R.id.RoomName);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance(getString(R.string.firebase_realtime_db_url));
        roomRef = database.getReference("rooms");
        userRoomRef = database.getReference("user_rooms");
        intent=getIntent();
        if (intent!=null){
            roomCode=intent.getStringExtra("room_code");

        }
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        roomRef.orderByChild("room_code").equalTo(roomCode.trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    roomData = data.getValue(RoomData.class);
                    roomName.setText(roomData.getRoom_name());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to load rooms", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        fileList = new ArrayList<>();
        loadFilesFromFirebase();


    }
    private void loadFilesFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);

//        StorageReference storageRef = storage.getReference().child("uploads/"); // Folder name in Firebase Storage
//
//        storageRef.listAll().addOnSuccessListener(listResult -> {
//            for (StorageReference fileRef : listResult.getItems()) {
//                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                    fileList.add(new FileModel(fileRef.getName(), uri.toString()));
//                    fileAdapter.notifyDataSetChanged();
//                }).addOnFailureListener(e -> Log.e("Firebase", "Failed to get file URL", e));
//            }
//            progressBar.setVisibility(View.GONE);
//        }).addOnFailureListener(e -> {
//            Toast.makeText(this, "Failed to load files", Toast.LENGTH_SHORT).show();
//            progressBar.setVisibility(View.GONE);
//        });
//
//        fileAdapter = new FileAdapter(this, fileList);
//        recyclerView.setAdapter(fileAdapter);
    }
}