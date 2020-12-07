package com.e.androidpuzzlegame;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Switch switchSign;
    private TextView textSign;
    private Button buttonSign;
    private RelativeLayout nicknameInput;
    private RelativeLayout confirmPasswodInput;
    private EditText usernameText;// = android:id="@+id/username_text";
    private EditText nicknameText;
    private EditText passwordText;
    private EditText confirmPasswordText;

    private boolean signup = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
//        startActivity(intent);

        setContentView(R.layout.sign_layout);
        login();
    }

    private void login() {
        switchSign = findViewById(R.id.switchSign);
        textSign = findViewById(R.id.textSign);
        buttonSign = findViewById(R.id.buttonSign);
        nicknameInput = findViewById(R.id.nickname_input);
        confirmPasswodInput = findViewById(R.id.confirm_passwod_input);

        usernameText =  findViewById(R.id.username_text);
        nicknameText = findViewById(R.id.nickname_text);
        passwordText = findViewById(R.id.password_text);
        confirmPasswordText = findViewById(R.id.confirm_password_text);

        switchSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchSign.setText("Sign Up");
                    textSign.setText("Sign In");
                    buttonSign.setText("Sign In");
                    nicknameInput.setVisibility(View.GONE);
                    confirmPasswodInput.setVisibility(View.GONE);
                    signup = false;
                    usernameText.setText("");
                    nicknameText.setText("");
                    passwordText.setText("");
                    confirmPasswordText.setText("");
                } else {
                    switchSign.setText("Sign In");
                    textSign.setText("Sign Up");
                    buttonSign.setText("Sign Up");
                    nicknameInput.setVisibility(View.VISIBLE);
                    confirmPasswodInput.setVisibility(View.VISIBLE);
                    signup = true;
                    usernameText.setText("");
                    nicknameText.setText("");
                    passwordText.setText("");
                    confirmPasswordText.setText("");
                }

            }
        });

        buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signup == true) {
                    if (usernameText.getText().toString().matches("^[a-z0-9]*$") && usernameText.getText().toString().length() >= 5) {
                        usernameText.setError(null);
                        if (nicknameText.getText().toString().matches("^[a-z0-9]*$") && nicknameText.getText().toString().length() >= 2) {
                            nicknameText.setError(null);
                            if (passwordText.getText().toString().length() >= 4) {
                                passwordText.setError(null);
                                if (passwordText.getText().toString().equals(confirmPasswordText.getText().toString())) {
                                    confirmPasswordText.setError(null);
                                    Toast.makeText(MainActivity.this, "Sign up", Toast.LENGTH_LONG).show();
                                } else {
                                    confirmPasswordText.setError("Passwords must match.");
                                }
                            } else {
                                passwordText.setError("The password must be longer than 3 characters.");
                            }
                        } else {
                            nicknameText.setError("The nickname must contain just letters or numbers and must be longer than 1 characters");
                        }
                    } else {
                        usernameText.setError("The username must contain just letters or numbers and must be longer than 4 characters.");
                    }
                } else {

                    Toast.makeText(MainActivity.this, "Sign in or invalid username or password.", Toast.LENGTH_LONG).show();


//                    if (usernameText.getText().toString().matches("^[a-z0-9]*$") && usernameText.getText().toString().length() >= 5) {
//                        usernameText.setError(null);
//                        if (passwordText.getText().toString().length() >= 6) {
//                            passwordText.setError(null);
//                            Toast.makeText(MainActivity.this, "Sign in", Toast.LENGTH_LONG).show();
//                        } else {
//                            passwordText.setError("The password must be longer than 4 characters.");
//                        }
//                    } else {
//                        usernameText.setError("The username must contain just letters or numbers and must be longer than 4 characters.");
//                    }


                }
            }
        });
    }


}