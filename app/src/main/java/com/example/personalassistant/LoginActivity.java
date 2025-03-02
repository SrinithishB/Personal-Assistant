package com.example.personalassistant;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    Button login;
    EditText emailText,passwordText;
    TextView signupText,forgotPasswordText;
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
        forgotPasswordText=findViewById(R.id.forgotPasswordLink);
        login.setOnClickListener(view -> loginUser());
        signupText.setOnClickListener(view -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
        forgotPasswordText.setOnClickListener(v->showResetPasswordDialog());
    }
    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);

        // Customizing the title text color
        SpannableString title = new SpannableString("Reset Password");
        title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, title.length(), 0);

        // Customizing the message text color
        SpannableString message = new SpannableString("Enter your email to receive a password reset link.");
        message.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, message.length(), 0);

        builder.setTitle(title);
        builder.setMessage(message);

        // Create an EditText field with modern styling
        final EditText emailInput = new EditText(this);
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setHint("Enter your email");
        emailInput.setTextColor(getResources().getColor(R.color.white));
        emailInput.setHintTextColor(getResources().getColor(R.color.gray));
//        emailInput.setBackgroundResource(R.drawable.edittext_bg);  // Custom background for rounded edges
        emailInput.setPadding(30, 20, 30, 20);

        // Create a FrameLayout to add margins
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(40, 10, 40, 10);  // Left, Top, Right, Bottom margins
        emailInput.setLayoutParams(params);
        container.addView(emailInput);

        builder.setView(container);

        // Positive (Reset) Button
        builder.setPositiveButton("Reset", (dialog, which) -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Email is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            resetPassword(email);
        });

        // Negative (Cancel) Button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Customize Button Colors
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray));
    }


    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset link sent to your email!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
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