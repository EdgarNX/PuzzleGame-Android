package com.e.androidpuzzlegame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseUser;

public class SettingsActivity  extends AppCompatActivity {
    MediaPlayer music;
    private LinearLayout logoutButton;
    private TextView logoutTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        music = MediaPlayer.create(this, R.raw.background_sound);

        Switch musicSwitch = findViewById(R.id.musicSwitch);
        musicSwitch.setChecked(true);
        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                //start service and play music
                startService(new Intent(this, SoundService.class));

                Toast.makeText(this,"apasat",Toast.LENGTH_SHORT).show();
            }else{
                stopService(new Intent(this, SoundService.class));

                Toast.makeText(this,"dezapasat",Toast.LENGTH_SHORT).show();
            }
        });

        logoutButton = findViewById(R.id.logout_button);
        logoutTextView = findViewById(R.id.logout_text_view);

        Bundle bundle = getIntent().getExtras();
        
        String currentUsername = "";
        String currentNickname = "";
        
        
        if (bundle != null) {
            currentUsername = bundle.getString(MainActivity.USERNAME_MESSAGE_KEY);
            currentNickname = bundle.getString(MainActivity.NICKNAME_MESSAGE_KEY);
        }

        String logoutText = "Logout ";
        logoutText += currentNickname;
        logoutTextView.setText(logoutText);
        
        logoutButton.setOnClickListener(v -> {
            ParseUser.logOut();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        
    }
}
