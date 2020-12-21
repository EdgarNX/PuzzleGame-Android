package com.e.androidpuzzlegame;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity implements OnClickListener {

    public static final String USERNAME_MESSAGE_KEY = "username";
    public static final String NICKNAME_MESSAGE_KEY = "nickname";


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchSign;
    private TextView textSign;
    private Button buttonSign;
    private RelativeLayout nicknameInput;
    private RelativeLayout confirmPasswordInput;
    private EditText usernameText;
    private EditText nicknameText;
    private EditText passwordText;
    private EditText confirmPasswordText;
    private LinearLayout signLayout;
    private ImageView logo;

    Toast toast;

    private boolean signup = false;
    private String theCurrentUserNickname = "default";
    private String theCurrentUserUsername = "default";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_layout);

        if (ParseUser.getCurrentUser() != null) {
            Log.e("user", ParseUser.getCurrentUser().getUsername());

            ParseUser user = ParseUser.getCurrentUser();

            theCurrentUserUsername = user.getUsername();

            getTheUserNickname();
        }

        initialize();

        checkTheLayoutWhatToShow();

        sign();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void waitAndGo() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.e("Error Message: ", e.getMessage());
        }

    }

    private void initialize() {
        switchSign = findViewById(R.id.switchSign);
        textSign = findViewById(R.id.textSign);
        buttonSign = findViewById(R.id.buttonSign);
        nicknameInput = findViewById(R.id.nickname_input);
        confirmPasswordInput = findViewById(R.id.confirm_passwod_input);

        usernameText = findViewById(R.id.username_text);
        nicknameText = findViewById(R.id.nickname_text);
        passwordText = findViewById(R.id.password_text);
        confirmPasswordText = findViewById(R.id.confirm_password_text);

        signLayout = findViewById(R.id.sign_layout);

        signLayout.setOnClickListener(this);

        logo = findViewById(R.id.puzzle_logo);

        logo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        View view = this.getCurrentFocus();
        if (view != null) {
            if (v.getId() == R.id.sign_layout || v.getId() == R.id.puzzle_logo) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    private void checkTheLayoutWhatToShow() {
        switchSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchSign.setText("Sign Up");
                    textSign.setText("Sign In");
                    buttonSign.setText("Sign In");
                    nicknameInput.setVisibility(View.GONE);
                    confirmPasswordInput.setVisibility(View.GONE);
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
                    confirmPasswordInput.setVisibility(View.VISIBLE);
                    signup = true;
                    usernameText.setText("");
                    nicknameText.setText("");
                    passwordText.setText("");
                    confirmPasswordText.setText("");
                }
            }
        });
    }

    private void sign() {
        buttonSign.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signup) {
                    signUpFieldVerification();
                } else {
                    signInFieldVerification();
                }
            }
        });
    }

    private void getTheUserNickname() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", theCurrentUserUsername);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && !objects.isEmpty()) {
                    if (!objects.get(0).getString("nickname").isEmpty()) {

                        Log.e("FindInBackground", "Retrieved " + objects.get(0).getUsername());

                        theCurrentUserNickname = objects.get(0).getUsername();

                        Log.e("FindInBackground", "Retrieved " + theCurrentUserNickname);

                        goToTheNextActivity(theCurrentUserUsername, theCurrentUserNickname);
                    }
                } else {
                    Log.e("GetInBackgound", "Failed. Error: " + e.toString());
                }
            }
        });
    }

    private void goToTheNextActivity(String username, String nickname) {

        Intent afterSignIn = new Intent(MainActivity.this, AfterSignInActivity.class);
        String current_username = username;
        String current_nickname = nickname;

        if (current_username != null && current_username.length() > 0 && current_nickname != null && current_nickname.length() > 0) {
            afterSignIn.putExtra(USERNAME_MESSAGE_KEY, current_username);
            afterSignIn.putExtra(NICKNAME_MESSAGE_KEY, current_nickname);
        } else {
            buttonSign.setError(getString(R.string.error_missing_message));
        }
        startActivity(afterSignIn);
    }

    private void signInFieldVerification() {
        if (usernameText.getText().toString().length() >= 4) {
            usernameText.setError(null);
            if (passwordText.getText().toString().length() >= 4) {
                passwordText.setError(null);

                singInToDatabase();

            } else {
                passwordText.setError("Incorrect password format.");
            }
        } else {
            usernameText.setError("Incorrect username format.");
        }
    }

    private void singInToDatabase() {
        ParseUser.logInInBackground(usernameText.getText().toString(), passwordText.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    toast = Toast.makeText(getApplicationContext(), "SignIn successfully", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    theCurrentUserUsername = usernameText.getText().toString();

                    getTheUserNickname();

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } else {
                    toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }

        });
    }


    private void signUpFieldVerification() {
        if (usernameText.getText().toString().matches("^[a-z0-9]*$") && usernameText.getText().toString().length() >= 4) {
            usernameText.setError(null);

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("nickname", nicknameText.getText().toString());
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null && !objects.isEmpty()) {
                        if (!objects.get(0).getString("nickname").isEmpty()) {

                            toast = Toast.makeText(getApplicationContext(), "The nickname is already used.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }
                    } else {

                        if (nicknameText.getText().toString().matches("^[a-z0-9]*$") && nicknameText.getText().toString().length() >= 2) {
                            nicknameText.setError(null);
                            if (passwordText.getText().toString().length() >= 4) {
                                passwordText.setError(null);
                                if (passwordText.getText().toString().equals(confirmPasswordText.getText().toString())) {

                                    singUpToDatabase();

                                } else {
                                    confirmPasswordText.setError("Passwords must match.");
                                }
                            } else {
                                passwordText.setError("The password must be longer than 3 characters.");
                            }
                        } else {
                            nicknameText.setError("The nickname must contain just letters or numbers and must be longer than 1 characters.");
                        }

                    }
                }
            });
        } else {
            usernameText.setError("The username must contain just letters or numbers and must be longer than 3 characters.");
        }
    }

    private void singUpToDatabase() {
        ParseUser user = new ParseUser();

        user.setUsername(usernameText.getText().toString());
        user.setPassword(passwordText.getText().toString());
        user.put("nickname", nicknameText.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    toast = Toast.makeText(getApplicationContext(), "SignUp successfully", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    goToTheNextActivity(usernameText.getText().toString(), nicknameText.getText().toString());

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } else {
                    toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });
    }

}