package com.example.personalassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
CardView calculator,physical,todo,gps;
GridLayout gridLayout;
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
        calculator=findViewById(R.id.calculator_card);
        physical=findViewById(R.id.physicalActivityCard);
        gridLayout=findViewById(R.id.gridLayout);
        gps=findViewById(R.id.gpsCard);
        todo=findViewById(R.id.todoCard);
        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_top_slide);
        gridLayout.startAnimation(animation);
        calculator.setOnClickListener(v -> {
            Intent intent=new Intent(this,CalculatorActivity.class);
            startActivity(intent);
        });
        physical.setOnClickListener(v -> {
            Intent intent=new Intent(this, PhysicalActivity.class);
            startActivity(intent);
        });
        todo.setOnClickListener(v -> {
            Intent intent=new Intent(this, TodoActivity.class);
            startActivity(intent);
        });
        gps.setOnClickListener(v -> {
            Intent intent =new Intent(this, MapsActivity.class);
            startActivity(intent);
        });
    }
}