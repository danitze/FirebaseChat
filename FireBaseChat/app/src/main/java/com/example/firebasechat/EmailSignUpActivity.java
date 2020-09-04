package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class EmailSignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar progressBar;
    private EditText editTextPersonName, editTextEmailAddress, editTextPassword, editTextRepeatPassword;
    private Button signUpButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_signup);
        getSupportActionBar().hide();

        findViews();
        signUpButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void findViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        editTextPersonName = (EditText) findViewById(R.id.editTextPersonName);
        editTextEmailAddress = (EditText) findViewById(R.id.editTextEmailAddress);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextRepeatPassword = (EditText) findViewById(R.id.editTextRepeatPassword);

        signUpButton = (Button) findViewById(R.id.signUpButton);
    }

    private void registerUser() {
        final String name = editTextPersonName.getText().toString().trim();
        final String email = editTextEmailAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String repeatedPassword = editTextRepeatPassword.getText().toString().trim();

        if(name.isEmpty()) {
            editTextPersonName.setError(getString(R.string.blank_field));
            editTextPersonName.requestFocus();
        }

        else if(email.isEmpty()) {
            editTextEmailAddress.setError(getString(R.string.blank_field));
            editTextEmailAddress.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmailAddress.setError(getString(R.string.email_error));
            editTextEmailAddress.requestFocus();
        }

        else if(password.isEmpty()) {
            editTextPassword.setError(getString(R.string.blank_field));
            editTextPassword.requestFocus();
        }

        else if(password.length() < 6) {
            editTextPassword.setError(getString(R.string.short_password));
            editTextPassword.requestFocus();
        }

        else if(repeatedPassword.isEmpty()) {
            editTextRepeatPassword.setError(getString(R.string.blank_field));
            editTextRepeatPassword.requestFocus();
        }

        else if(!repeatedPassword.equals(password)) {
            editTextRepeatPassword.setError(getString(R.string.passwords_error));
            editTextRepeatPassword.requestFocus();
        }

        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                final FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if(task.isSuccessful()) {
                                            mAuth.signOut();
                                            Intent intent = new Intent(EmailSignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                        else {
                                            Toast.makeText(EmailSignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(EmailSignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) { //Only for sign up button
        AuxiliaryActions.hideKeyboard(this);
        registerUser();
    }
}