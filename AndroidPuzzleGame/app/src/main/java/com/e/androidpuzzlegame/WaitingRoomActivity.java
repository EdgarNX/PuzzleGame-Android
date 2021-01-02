package com.e.androidpuzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class WaitingRoomActivity extends AppCompatActivity {

    private TextView hostText;
    private TextView pl2Text;
    private TextView pl3Text;
    private TextView pl4Text;

    private Button buttonStart;
    private Button buttonStop;

    String currentTableName;
    String currentNickname;
    Boolean doneSettingTheCurrentPlayer = false;

    String host;
    String playerTwo;
    String playerThree;
    String playerFour;

    String thePlaceOfTheCurrentPlayer;

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.waiting_room_layout);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            currentTableName = bundle.getString(OnlineActivity.TABLE_MESSAGE_KEY);
            currentNickname = bundle.getString(OnlineActivity.NICKNAME_MESSAGE_KEY);
        }

        initialize();

        getDataFromDatabase();

        backOrStop();
//
//        ParseLiveQueryClient parseLiveQueryClient = null;
//
//        try {
//            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://puzzlegame.b4a.io/"));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//
//        // Message - Live Query
//        if (parseLiveQueryClient != null) {
//            ParseQuery<ParseObject> parseQuery = new ParseQuery("GameTable");
//            parseQuery.whereEqualTo("name", "currentTableName");
//            SubscriptionHandling<ParseObject> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
//
//
//
//            subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<ParseObject>() {
//                @Override
//                public void onEvent(ParseQuery<ParseObject> query, ParseObject object) {
//                    Handler handler = new Handler(Looper.getMainLooper());
//                    handler.post(new Runnable() {
//                        public void run() {
//                            Log.e("let's see", object.get("playerTwo").toString());
//                            Log.e("let's see", object.get("playerThree").toString());
//                            Log.e("let's see", object.get("playerFour").toString());
//                        }
//
//                    });
//                }
//            });
//
//
//        }

    }


    public void backOrStop() {

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!currentNickname.equals(host)) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("GameTable");
                    query.whereEqualTo("name", currentTableName);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object != null) {
                                object.put(thePlaceOfTheCurrentPlayer, "00");
                                object.saveInBackground();

                                onBackPressed();
                            } else {
                                Log.e("error", "something went wrong");
                                Log.e("error message", e.getMessage());
                            }
                        }
                    });
                } else {
                    //TODO implementation for this, when the host destroy the room
                    // this also will need to trigger the other users
                    Log.e("no implementation", "yet");
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void getDataFromDatabase() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameTable");
        query.whereEqualTo("name", currentTableName);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object != null) {
                    host = object.get("host").toString();
                    playerTwo = object.get("playerTwo").toString();
                    playerThree = object.get("playerThree").toString();
                    playerFour = object.get("playerFour").toString();

                    hostText.setText(host);
                    if (!"00".equals(playerTwo)) {
                        pl2Text.setText(playerTwo);
                    }
                    if (!"00".equals(playerThree)) {
                        pl3Text.setText(playerThree);
                    }

                    Log.e("players", host + playerTwo + playerThree + playerFour);
                    Log.e("found", "we good");

                    setPlayers();
                } else {
                    Log.e("error", "something went wrong");
                    Log.e("error message", e.getMessage());
                }
            }
        });
    }

    private void setPlayers() {
        Log.e("error message", currentNickname + " " + host);

        if (currentNickname.equals(host)) {
            hostText.setText(currentNickname);
            doneSettingTheCurrentPlayer = true;

            buttonStop.setText("DROP ROOM");
        } else if ("00".equals(playerTwo) && !doneSettingTheCurrentPlayer) {
            pl2Text.setText(currentNickname);
            setThePlayerInTheDatabase("playerTwo", true);
            thePlaceOfTheCurrentPlayer = "playerTwo";
            doneSettingTheCurrentPlayer = true;

            buttonStop.setText("QUIT");
        } else if ("00".equals(playerThree) && !doneSettingTheCurrentPlayer) {
            pl3Text.setText(currentNickname);
            setThePlayerInTheDatabase("playerThree", true);
            thePlaceOfTheCurrentPlayer = "playerThree";
            doneSettingTheCurrentPlayer = true;

            buttonStop.setText("QUIT");
        } else {
            pl4Text.setText(currentNickname);
            setThePlayerInTheDatabase("playerFour", false);
            thePlaceOfTheCurrentPlayer = "playerFour";
            doneSettingTheCurrentPlayer = true;

            buttonStop.setText("QUIT");
        }
    }

    private void setThePlayerInTheDatabase(String playerPlace, Boolean available) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameTable");
        query.whereEqualTo("name", currentTableName);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null) {

                    if ("playerTwo".equals(playerPlace)) {
                        objects.get(0).put("playerTwo", currentNickname);
                    } else if ("playerThree".equals(playerPlace)) {
                        objects.get(0).put("playerThree", currentNickname);
                    } else {
                        objects.get(0).put("playerFour", currentNickname);
                    }

                    if (available == false) {
                        objects.get(0).put("available", "false");
                    }

                    objects.get(0).saveInBackground();
                }
            }
        });
    }

    private void initialize() {
        hostText = findViewById(R.id.hostText);
        pl2Text = findViewById(R.id.pl1Text);
        pl3Text = findViewById(R.id.pl2Text);
        pl4Text = findViewById(R.id.pl3Text);

        buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setVisibility(View.GONE);

        buttonStop = findViewById(R.id.buttonStop);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.waiting_room, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.waiting_room);

        SpannableString s = new SpannableString("Waiting Room");

        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);

        item.setTitle(s);

        return super.onPrepareOptionsMenu(menu);
    }

}