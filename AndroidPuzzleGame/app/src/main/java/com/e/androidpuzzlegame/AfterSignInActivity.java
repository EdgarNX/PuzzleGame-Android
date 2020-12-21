package com.e.androidpuzzlegame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseAnalytics;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class AfterSignInActivity extends AppCompatActivity {

    private Button logout_button;

    private String currentUsername;
    private String currentNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.after_sign_in);

        Log.e("saVedem", "am ajuns inaintea ta");

        initialize();
    }

    private void initialize() {
        logout_button = findViewById(R.id.logout_button);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            currentUsername = bundle.getString(MainActivity.USERNAME_MESSAGE_KEY);
            currentNickname = bundle.getString(MainActivity.NICKNAME_MESSAGE_KEY);

            Log.e("saVedem", currentUsername);
            Log.e("saVedem", currentNickname);

        }

        String logoutText = "Logout ";
        logoutText += currentNickname + " " + currentUsername;
        logout_button.setText(logoutText);
    }

    public void logout(View view) {
        ParseUser.logOut();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
