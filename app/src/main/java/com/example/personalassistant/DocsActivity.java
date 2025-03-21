package com.example.personalassistant;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DocsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    RecyclerView recyclerView;
    RoomsRecyclerListAdapter recyclerListAdapter;
    List<RoomData> roomDataList;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton floatingActionButton;
    DatabaseReference roomRef, userRoomRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_docs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        floatingActionButton = findViewById(R.id.floatingActionBtn);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance(getString(R.string.firebase_realtime_db_url));
        roomRef = database.getReference("rooms");
        userRoomRef = database.getReference("user_rooms");

        if (user == null) {
            Toast.makeText(this, "Please log in to access this feature", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        CoordinatorLayout parentLayout = findViewById(R.id.CoordinatorLayout);
        View recyclerClass = LayoutInflater.from(getApplicationContext()).inflate(R.layout.rooms_recycler_list_layout, parentLayout, false);
        parentLayout.addView(recyclerClass);
        recyclerView = recyclerClass.findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadUserRooms();
            swipeRefreshLayout.setRefreshing(false);
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomDataList = new ArrayList<>();
        recyclerListAdapter = new RoomsRecyclerListAdapter(this, roomDataList);
        recyclerView.setAdapter(recyclerListAdapter);
        loadUserRooms();

        floatingActionButton.setOnClickListener(view -> {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                JoinRoomBottomSheetActivity joinRoomBottomSheet = new JoinRoomBottomSheetActivity();
                joinRoomBottomSheet.show(getSupportFragmentManager(), "Join room and Create room");
            } else {
                Snackbar.make(findViewById(R.id.CoordinatorLayout), getString(R.string.connect_internet), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserRooms() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRoomRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomDataList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    RoomData roomData = data.getValue(RoomData.class);
                    roomDataList.add(roomData);
                }
                recyclerListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to load rooms", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
