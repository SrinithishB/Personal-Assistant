package com.example.personalassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
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
    CardView calculator, physical, todo, gps, docs, notes;
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
        docs = findViewById(R.id.documentsCard);
        notes = findViewById(R.id.notesCard);

        if (user == null) {
            constraintLayout.setVisibility(View.GONE);
        } else {
            constraintLayout.setVisibility(View.VISIBLE);
            String name = user.getDisplayName();
            nameText.setText("Hi, " + name + "!");
        }

        // Apply fade-in effect to greeting and profile
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        nameText.startAnimation(fadeIn);
        profilePic.startAnimation(fadeIn);

        // Apply slide-in effect to GridLayout
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.bottom_top_slide);
        gridLayout.startAnimation(slideIn);

        // Apply bounce effect on card click
        applyBounceEffect(calculator);
        applyBounceEffect(physical);
        applyBounceEffect(todo);
        applyBounceEffect(gps);
        applyBounceEffect(docs);
        applyBounceEffect(notes);

//        calculator.setOnClickListener(v -> startActivity(new Intent(this, CalculatorActivity.class)));
//        physical.setOnClickListener(v -> startActivity(new Intent(this, PhysicalActivity.class)));
//        todo.setOnClickListener(v -> startActivity(new Intent(this, TodoActivity.class)));
//        gps.setOnClickListener(v -> startActivity(new Intent(this, WeatherActivity.class)));
//        docs.setOnClickListener(v -> startActivity(new Intent(this, DocsActivity.class)));
//        notes.setOnClickListener(v -> startActivity(new Intent(this, NotesActivity.class)));

        profilePic.setOnClickListener(view -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    // Helper function to add bounce animation on click
    private void applyBounceEffect(CardView cardView) {
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        cardView.setOnClickListener(v -> {
            v.startAnimation(bounce);  // ✅ Apply animation on click
            v.postDelayed(() -> {
                // Open the respective activity after animation
                Intent intent = null;
                if (v.getId() == R.id.calculator_card) {
                    intent = new Intent(this, CalculatorActivity.class);
                } else if (v.getId() == R.id.physicalActivityCard) {
                    intent = new Intent(this, PhysicalActivity.class);
                } else if (v.getId() == R.id.todoCard) {
                    intent = new Intent(this, TodoActivity.class);
                } else if (v.getId() == R.id.gpsCard) {
                    intent = new Intent(this, WeatherActivity.class);
                } else if (v.getId() == R.id.documentsCard) {
                    intent = new Intent(this, DocsActivity.class);
                } else if (v.getId() == R.id.notesCard) {
                    intent = new Intent(this, NotesActivity.class);
                }

                if (intent != null) {
                    startActivity(intent);
                }
            }, 300); // Wait for animation before opening new activity
        });
    }

}
