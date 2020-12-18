package com.e.androidpuzzlegame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity  extends AppCompatActivity {
    MediaPlayer music;
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
    }

}
