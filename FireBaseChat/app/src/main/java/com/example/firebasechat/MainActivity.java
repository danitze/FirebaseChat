package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText editTextEmailAddress, editTextPassword;

    private TextView forgotPasswordText, promptingText;

    private Button signInButton;

    private CheckBox rememberPassword;

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    private final static String IF_PASSWORD_SAVED_FILE = "Save password";
    private final static String USER_DATA_FILE = "User data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        initializeViews();

        checkBoxStatus();

        setOnClickListeners();

        restoreData();
    }

    private void initializeViews() {
        editTextEmailAddress = (EditText) findViewById(R.id.editTextEmailAddress);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        forgotPasswordText = (TextView) findViewById(R.id.forgotPasswordText);
        promptingText = (TextView) findViewById(R.id.promptingText);

        signInButton = (Button) findViewById(R.id.signInButton);

        rememberPassword = (CheckBox) findViewById(R.id.rememberPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void setOnClickListeners() {
        signInButton.setOnClickListener(this);

        forgotPasswordText.setOnClickListener(this);
        promptingText.setOnClickListener(this);

        rememberPassword.setOnCheckedChangeListener(this);
    }

    private void checkBoxStatus() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.openFileInput(IF_PASSWORD_SAVED_FILE)));
            if(bufferedReader.readLine().equals("0")) {
                rememberPassword.setChecked(false);
            }
            else {
                rememberPassword.setChecked(true);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            new File(IF_PASSWORD_SAVED_FILE);
            saveData(IF_PASSWORD_SAVED_FILE, String.valueOf(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getUserData() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.openFileInput(USER_DATA_FILE)));
            String result = bufferedReader.readLine();
            bufferedReader.close();
            return result;
        } catch (FileNotFoundException e) {
            new File(USER_DATA_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveData(String fileName, String data) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.openFileOutput(fileName, MODE_PRIVATE)));
            bufferedWriter.write(data);
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            new File(fileName);
            saveData(fileName, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restoreData() {
        if(rememberPassword.isChecked()) {
            try {
                String[] userData = getUserData().split(" ");
                editTextEmailAddress.setText(userData[0]);
                editTextPassword.setText(userData[1]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            String email = getUserData();
            editTextEmailAddress.setText(email);
        }
    }

    private void updateUserDataSaved(String email, String password) {
        if(email == null)
            email = "";
        if(password == null)
            password = "";
        if(rememberPassword.isChecked()) {
            saveData(USER_DATA_FILE, email + " " + password);
        }
        else {
            saveData(USER_DATA_FILE, email);
        }
    }

    private void signInUser() {
        String email = editTextEmailAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()) {
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
        else {
            updateUserDataSaved(email, password);
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.promptingText:
                updateUserDataSaved(editTextEmailAddress.getText().toString(), editTextPassword.getText().toString());
                Intent intent = new Intent(MainActivity.this, EmailSignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.signInButton:
                AuxiliaryActions.hideKeyboard(this);
                signInUser();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b) {
            Log.d("Button", "" + 1);
            saveData(IF_PASSWORD_SAVED_FILE, String.valueOf(1));
        }
        else {
            Log.d("Button", "" + 0);
            saveData(IF_PASSWORD_SAVED_FILE, String.valueOf(0));
        }
    }
}