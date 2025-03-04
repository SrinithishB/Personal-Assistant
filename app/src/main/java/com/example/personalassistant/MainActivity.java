package com.example.personalassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    CardView calculator, physical, todo, gps;
    private FirebaseAuth mAuth;
    GridLayout gridLayout;
    TextView nameText;
    ImageView profilePic;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        constraintLayout = findViewById(R.id.constraintLayout);
        calculator = findViewById(R.id.calculator_card);
        physical = findViewById(R.id.physicalActivityCard);
        gridLayout = findViewById(R.id.gridLayout);
        gps = findViewById(R.id.gpsCard);
        todo = findViewById(R.id.todoCard);
        nameText = findViewById(R.id.userNameText);
        profilePic = findViewById(R.id.profilePic);

        if (user == null) {
            constraintLayout.setVisibility(View.GONE);
//            Toast.makeText(this, "Please log in to access features", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//            return;
        } else {
            constraintLayout.setVisibility(View.VISIBLE);
            String name = user.getDisplayName();
            nameText.setText("Hi, " + name + "!");
        }

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_top_slide);
        gridLayout.startAnimation(animation);

        calculator.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalculatorActivity.class);
            startActivity(intent);
        });

        physical.setOnClickListener(v -> {
            Intent intent = new Intent(this, PhysicalActivity.class);
            startActivity(intent);
        });

        todo.setOnClickListener(v -> {
            Intent intent = new Intent(this, TodoActivity.class);
            startActivity(intent);
        });

        gps.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        });

        profilePic.setOnClickListener(view -> {
//            FirebaseAuth.getInstance().signOut();
//            Toast.makeText(this, "Logout Success!!!", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }
}
