package com.e.androidpuzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class OnlineActivity extends AppCompatActivity implements View.OnClickListener {
    private String nickname = "";
    private EditText createRoomEditText;
    private EditText joinRoomEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        Bundle extras = getIntent().getExtras();
        nickname = extras.getString("currentNickname");

        RelativeLayout activityOnline = findViewById(R.id.online_relative_layout);
        activityOnline.setOnClickListener(this);

        Button createButton = findViewById(R.id.create_button);
        createRoomEditText = findViewById(R.id.create_room_edit_text);
        createButton.setOnClickListener(v -> {
            if (createRoomEditText.getText().toString().matches("^[a-z0-9]*$") && createRoomEditText.getText().toString().length() >= 4) {
                createRoomEditText.setError(null);
                Intent intent = new Intent(this, SelectActivity.class);
                intent.putExtra("online", 1);
                startActivity(intent);
            } else {
                createRoomEditText.setError("The room name only contain letters/numbers and must > 3 characters!");
            }
        });
        Button joinButton = findViewById(R.id.join_button);
        joinRoomEditText = findViewById(R.id.join_room_edit_text);
        joinButton.setOnClickListener(v -> {
            if (joinRoomEditText.getText().toString().matches("^[a-z0-9]*$") && joinRoomEditText.getText().toString().length() >= 4) {
                joinRoomEditText.setError(null);

//                TODO put intent to waiting room

//                Intent intent = new Intent(this, SelectActivity.class);
//                intent.putExtra("online", 1);
//                startActivity(intent);
            } else {
                joinRoomEditText.setError("The room name only contain letters/numbers and must > 3 characters!");
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nickname_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.nickname_text);
        item.setTitle(nickname);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        View view = this.getCurrentFocus();
        if (view != null) {
            if (v.getId() == R.id.online_relative_layout) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}