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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    EditText emailText, passText, confirmPassText;
    Button btnSignup;
    TextView loginText;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        emailText = findViewById(R.id.emailText);
        passText = findViewById(R.id.passText);
        confirmPassText = findViewById(R.id.confirmPassText);
        btnSignup = findViewById(R.id.btnSignup);
        loginText = findViewById(R.id.loginText);

        mDialog = new ProgressDialog(this);
        
        btnSignup.setOnClickListener( v -> signUp());
        loginText.setOnClickListener( v -> logIn());
    }

    private void logIn() {
        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
    }

    private void signUp() {
        String email = emailText.getText().toString();
        String password = passText.getText().toString();
        String confirmPassword = confirmPassText.getText().toString();

        if (isValidated(email,password,confirmPassword)){
            mDialog.setMessage("Processing");
            userAuth(email,password);
        }
        
    }

    private void userAuth(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showToast("Verify Email");
                                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                            } else {
                                                showToast(task.getException().getMessage());
                                            }
                                        }
                                    });
                        } else {
                            mDialog.dismiss();
                            try{
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e){
                                passText.setError("Password is too weak");
                                passText.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                emailText.setError("Email ID is invalid or already in use..");
                                emailText.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e){
                                emailText.setError("Email ID is already registered...");
                                emailText.requestFocus();
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                showToast(e.getMessage());
                            }
                            showToast(task.getException().getMessage());
                        }
                    }
                });
    }

    private boolean isValidated(String email, String password, String confirmPassword) {
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
            passText.setError("Password length should be less than 7 characters");
            passText.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(confirmPassword)){
            confirmPassText.setError("Confirm Password cannot be empty");
            confirmPassText.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPassText.setError("Password not matched");
            confirmPassText.requestFocus();
            return false;
        }
        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}