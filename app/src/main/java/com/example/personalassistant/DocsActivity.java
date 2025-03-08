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

import java.util.ArrayList;
import java.util.List;

public class DocsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    RecyclerView recyclerView;
    RoomsRecyclerListAdapter recyclerListAdapter;
    List<RoomData> roomDataList;
    SwipeRefreshLayout swipeRefreshLayout;
//    public List<ClassNameList> arrayLists;
    FloatingActionButton floatingActionButton;


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
        floatingActionButton=findViewById(R.id.floatingActionBtn);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in to access this features", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        CoordinatorLayout parentLayout=findViewById(R.id.CoordinatorLayout);
        View recyclerClass= LayoutInflater.from(getApplicationContext()).inflate(R.layout.rooms_recycler_list_layout,parentLayout,false);
        parentLayout.addView(recyclerClass);
        recyclerView=recyclerClass.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomDataList=new ArrayList<>(){{
            add(new RoomData("R001", "Living Room", "John Doe", "johndoe@example.com", "LVR123", "2025-03-07", 1));
            add(new RoomData("R002", "Kitchen", "Jane Smith", "janesmith@example.com", "KCH456", "2025-03-06", 2));
            add(new RoomData("R003", "Bedroom", "Alice Brown", "alicebrown@example.com", "BDR789", "2025-03-05", 3));
            add(new RoomData("R004", "Bathroom", "Bob White", "bobwhite@example.com", "BTH101", "2025-03-04", 4));
            add(new RoomData("R005", "Office", "Charlie Green", "charliegreen@example.com", "OFF202", "2025-03-03", 5));
        }};
        recyclerListAdapter=new RoomsRecyclerListAdapter(this,roomDataList);
        recyclerView.setAdapter(recyclerListAdapter);
        floatingActionButton.setOnClickListener(view -> {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                JoinRoomBottomSheetActivity joinRoomBottomSheet=new JoinRoomBottomSheetActivity();
                joinRoomBottomSheet.show(getSupportFragmentManager(),"Join room and Create room");
            }
            else {
                Snackbar.make(findViewById(R.id.CoordinatorLayout),getString(R.string.connect_internet), Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}