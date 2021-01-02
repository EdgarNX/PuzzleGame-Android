package com.e.androidpuzzlegame;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GameModeActivity extends AppCompatActivity {

    private String currentUsername = "";
    private String currentNickname = "";

    protected void onCreate(Bundle savedInstanceState) {
        PlayBackgroundSound();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);

        Button singlePlayer = findViewById(R.id.single_player);
        Button online = findViewById(R.id.online);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUsername = bundle.getString(MainActivity.USERNAME_MESSAGE_KEY);
            currentNickname = bundle.getString(MainActivity.NICKNAME_MESSAGE_KEY);
        }

        singlePlayer.setOnClickListener(v -> startActivity(new Intent(GameModeActivity.this, SelectActivity.class)));
        online.setOnClickListener(v -> {
            Intent intentOnlineActivity = new Intent(GameModeActivity.this, OnlineActivity.class);
            intentOnlineActivity.putExtra("currentNickname", currentNickname);
            startActivity(intentOnlineActivity);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.settings_menu, menu);

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingIntent = new Intent(this, SettingsActivity.class);

                settingIntent.putExtra(MainActivity.USERNAME_MESSAGE_KEY, currentUsername);
                settingIntent.putExtra(MainActivity.NICKNAME_MESSAGE_KEY, currentNickname);

                startActivity(settingIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void PlayBackgroundSound() {
        Intent intent = new Intent(this, SoundService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        //stop service and stop music
        stopService(new Intent(this, SoundService.class));
        super.onDestroy();
    }
}
