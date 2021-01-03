package com.e.androidpuzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class OnlineActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TABLE_MESSAGE_KEY = "currentTableName";
    public static final String NICKNAME_MESSAGE_KEY = "currentNickname";

    private String nickname = "";
    private EditText createRoomEditText;
    private EditText joinRoomEditText;

    Toast toast;

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

                ParseQuery<ParseObject> query = ParseQuery.getQuery("GameTable");
                query.whereEqualTo("name", createRoomEditText.getText().toString());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (!objects.isEmpty()) {
                                toast = Toast.makeText(getApplicationContext(), "The room already exists!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            } else {
                                createRoom();
                            }
                        } else {
                            toast = Toast.makeText(getApplicationContext(), "Something went wrong11111!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            joinRoomEditText.setText("");
                            createRoomEditText.setText("");
                        }
                    }
                });
            } else {
                createRoomEditText.setError("The room name only contain letters/numbers and must > 3 characters!");
                joinRoomEditText.setText("");
                createRoomEditText.setText("");
            }
        });

        Button joinButton = findViewById(R.id.join_button);
        joinRoomEditText = findViewById(R.id.join_room_edit_text);

        joinButton.setOnClickListener(v -> {
            if (joinRoomEditText.getText().toString().matches("^[a-z0-9]*$") && joinRoomEditText.getText().toString().length() >= 4) {
                joinRoomEditText.setError(null);
                joinRoom();
            } else {
                joinRoomEditText.setError("The room name only contain letters/numbers and must > 3 characters!");
            }
        });
    }

    public void joinRoom() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameTable");
        query.whereEqualTo("name", joinRoomEditText.getText().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && !objects.isEmpty()) {
                    if ("true".equals(objects.get(0).get("available").toString())) {
                        Intent intent = new Intent(OnlineActivity.this, WaitingRoomActivity.class);
                        intent.putExtra(TABLE_MESSAGE_KEY, joinRoomEditText.getText().toString());
                        intent.putExtra(NICKNAME_MESSAGE_KEY, nickname);
                        startActivity(intent);

                        joinRoomEditText.setText("");
                        createRoomEditText.setText("");
                    } else {
                        toast = Toast.makeText(getApplicationContext(), "The room is not available!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        joinRoomEditText.setText("");
                        createRoomEditText.setText("");
                    }
                } else {
                    toast = Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    joinRoomEditText.setText("");
                    createRoomEditText.setText("");
                }
            }
        });
    }

    public void createRoom() {
        ParseObject gameTable = new ParseObject("GameTable");
        gameTable.put("name", createRoomEditText.getText().toString());
        gameTable.put("host", nickname);
        gameTable.put("playerTwo", "00");
        gameTable.put("playerThree", "00");
        gameTable.put("playerFour", "00");
        gameTable.put("triggerDestroy", "no");
        gameTable.put("triggerStop", "no");
        gameTable.put("triggerStart", "no");
        gameTable.put("plOneTime", "no");
        gameTable.put("plTwoTime", "no");
        gameTable.put("plThreeTime", "no");
        gameTable.put("plFourTime", "no");
        gameTable.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("SaveInBackgound", "Successful");

                    Intent intent = new Intent(OnlineActivity.this, SelectActivity.class);
                    intent.putExtra("online", 1);
                    startActivity(intent);

                    joinRoomEditText.setText("");
                    createRoomEditText.setText("");
                } else {
                    Log.i("SaveInBackgound", "Failed. Error: " + e.toString());

                    joinRoomEditText.setText("");
                    createRoomEditText.setText("");
                }
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nickname_text:
                Intent settingIntent = new Intent(this, SettingsActivity.class);

                settingIntent.putExtra(MainActivity.NICKNAME_MESSAGE_KEY, nickname);

                startActivity(settingIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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