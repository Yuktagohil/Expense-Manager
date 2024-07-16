package com.example.expensemanagerapp;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText emailText, passText;
    Button btnLogin;
    TextView signupText;
    ProgressDialog mDialog;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        emailText = findViewById(R.id.emailText);
        passText = findViewById(R.id.passText);
        btnLogin = findViewById(R.id.btnLogin);
        signupText = findViewById(R.id.signupText);

        TextView forgetPassText = findViewById(R.id.forgetPassText);
        forgetPassText.setOnClickListener( v -> forgotPassword());

        mDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }

        btnLogin.setOnClickListener( v -> logIn());
        signupText.setOnClickListener( v -> signUp());

    }

    private void forgotPassword() {
        startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
    }

    private void signUp() {
        startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
    }

    private void logIn() {
        String email = emailText.getText().toString();
        String password = passText.getText().toString();

        if (isValidated(email,password)){
            mDialog.setMessage("Processing");
            userAuth(email,password);
        }
    }

    private void userAuth(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            mDialog.dismiss();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            showToast("Please Verify the Email");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Log.e(TAG,"onFailure: "+e.getMessage());
                        showToast(e.getLocalizedMessage());
                    }
                });
    }

    private boolean isValidated(String email, String password) {
        if(TextUtils.isEmpty(email)){
            emailText.setError("Email ID cannot be empty");
            emailText.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Email is invalid");
            emailText.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            passText.setError("Password cannot be empty");
            passText.requestFocus();
            return false;
        }
        if (password.length()<7) {
            passText.setError("Password length should be greater than 7 characters");
            passText.requestFocus();
            return false;
        }
        return true;
    }

    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}