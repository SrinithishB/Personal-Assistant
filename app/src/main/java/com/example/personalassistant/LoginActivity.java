package com.example.personalassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    Button login;
    EditText emailText,passwordText;
    TextView signupText;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        emailText=findViewById(R.id.emailText);
        passwordText=findViewById(R.id.passwordText);
        login=findViewById(R.id.loginBtn);
        signupText=findViewById(R.id.signupLink);
        login.setOnClickListener(view -> loginUser());
    }
   private void loginUser(){
       String email = emailText.getText().toString().trim();
       String password = passwordText.getText().toString().trim();

       if (email.isEmpty() || password.isEmpty()) {
           Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
           return;
       }

       mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(this, MainActivity.class));
               finish();
           } else {
               Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
           }
       });
   }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is already logged in, redirect to MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

}