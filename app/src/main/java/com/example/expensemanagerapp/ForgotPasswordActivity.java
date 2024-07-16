package com.example.expensemanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener( v -> resetPassword());

    }

    private void resetPassword() {

        String email = etEmail.getText().toString();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this,"Password Reset Mail Sent", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener( v -> {
                    Toast.makeText(this,"Error: " +v.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}