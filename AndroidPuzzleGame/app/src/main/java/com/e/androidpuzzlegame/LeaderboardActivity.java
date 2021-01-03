package com.e.androidpuzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;

public class LeaderboardActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView firstPlace;
    private TextView secondPlace;
    private TextView thirdPlace;
    private TextView fourthPlace;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    private LinearLayout linearLayout4;
    private TextView textLeaderboard;
    private Button buttonBack;


    public String currentTableName;
    public String currentPlayerName;
    public String hostName;

    private double pl1Time = 0;
    private double pl2Time = 0;
    private double pl3Time = 0;
    private double pl4Time = 0;


    private String pl1Name;
    private String pl2Name;
    private String pl3Name;
    private String pl4Name;

    private double[] playersTimes = new double[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_layout);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            currentTableName = bundle.getString(WaitingRoomActivity.ROOM_NAME_MESSAGE_KEY);
            currentPlayerName = bundle.getString(OnlineActivity.NICKNAME_MESSAGE_KEY);
        }

        initialize();

        backOrStop();

        verifyTheSituation();

        theAliveQuery();
    }

    private void backOrStop() {

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!currentPlayerName.equals(hostName)) {
                    Log.e("no", "host");
                    Intent intent = new Intent(LeaderboardActivity.this, GameModeActivity.class);
                    intent.putExtra(MainActivity.NICKNAME_MESSAGE_KEY, currentPlayerName);
                    startActivity(intent);

                } else {
                    Log.e("yes", "host");
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("GameTable");
                    query.whereEqualTo("name", currentTableName);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object != null) {
                                object.put("triggerDestroy", "yes");
                                object.saveInBackground();

                                object.deleteInBackground();
                            } else {
                                Log.e("error", "something went wrong");
                                Log.e("error message", e.getMessage());
                            }
                        }
                    });

                }
            }
        });

    }

    private void initialize() {
        firstPlace = findViewById(R.id.firstPlace);
        secondPlace = findViewById(R.id.secondPlace);
        thirdPlace = findViewById(R.id.thirdPlace);
        fourthPlace = findViewById(R.id.fourthPlace);

        linearLayout1 = findViewById(R.id.linearLayout);
        linearLayout2 = findViewById(R.id.linearLayout2);
        linearLayout3 = findViewById(R.id.linearLayout3);
        linearLayout4 = findViewById(R.id.linearLayout4);

        textLeaderboard = findViewById(R.id.textLeaderboard);
        buttonBack = findViewById(R.id.buttonBack);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void theAliveQuery() {
        ParseLiveQueryClient parseLiveQueryClient = null;

        SubscriptionHandling<ParseObject> subscriptionHandling;

        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://puzzlegame.b4a.io/"));

            ParseQuery<ParseObject> liveQuery = new ParseQuery("GameTable");
            liveQuery.whereEqualTo("name", currentTableName);

            subscriptionHandling = parseLiveQueryClient.subscribe(liveQuery);

            /**
             * Handle the subscription, if any new UPDATE event occur
             */
            subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<ParseObject>() {
                @Override
                public void onEvent(ParseQuery<ParseObject> query, final ParseObject object) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {

                            if (object != null) {

                                hostName = object.get("host").toString();

                                if (!object.get("plOneTime").toString().equals("no")) {
                                    playersTimes[1] = Double.parseDouble(object.get("plOneTime").toString());
                                    pl1Time = Double.parseDouble(object.get("plOneTime").toString());
                                    pl1Name = object.get("host").toString();
                                }

                                if (!object.get("plTwoTime").toString().equals("no")) {
                                    playersTimes[2] = Double.parseDouble(object.get("plTwoTime").toString());
                                    pl2Time = Double.parseDouble(object.get("plTwoTime").toString());
                                    pl2Name = object.get("playerTwo").toString();
                                }

                                if (!object.get("plThreeTime").toString().equals("no")) {
                                    playersTimes[3] = Double.parseDouble(object.get("plThreeTime").toString());
                                    pl3Time = Double.parseDouble(object.get("plThreeTime").toString());
                                    pl3Name = object.get("playerThree").toString();
                                }

                                if (!object.get("plFourTime").toString().equals("no")) {
                                    playersTimes[4] = Double.parseDouble(object.get("plFourTime").toString());
                                    pl4Time = Double.parseDouble(object.get("plFourTime").toString());
                                    pl4Name = object.get("playerFour").toString();
                                }

                                if (!object.get("triggerDestroy").toString().equals("no")) {
                                    Intent intent = new Intent(LeaderboardActivity.this, GameModeActivity.class);
                                    intent.putExtra(MainActivity.NICKNAME_MESSAGE_KEY, currentPlayerName);
                                    startActivity(intent);
                                }

                                verifyTheSituation();
                            }

                        }
                    });
                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("error live query", e.getMessage().toString());
        }
    }

    public void verifyTheSituation() {
        if (pl1Time != 0 && pl2Time != 0 && pl3Time != 0 && pl4Time != 0) {
            progressBar.setVisibility(View.GONE);

            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            linearLayout3.setVisibility(View.VISIBLE);
            linearLayout4.setVisibility(View.VISIBLE);

            textLeaderboard.setVisibility(View.VISIBLE);
            buttonBack.setVisibility(View.VISIBLE);


            double aux;
            for (int i = 1; i <= 3; i++) {
                for (int j = i + 1; j <= 4; j++) {
                    if (playersTimes[i] > playersTimes[j]) {
                        aux = playersTimes[i];
                        playersTimes[i] = playersTimes[j];
                        playersTimes[j] = aux;
                    }
                }
            }

            firstPlace.setText(chooseOne(playersTimes[1]));
            secondPlace.setText(chooseOne(playersTimes[2]));
            thirdPlace.setText(chooseOne(playersTimes[3]));
            fourthPlace.setText(chooseOne(playersTimes[4]));

        } else {
            progressBar.setVisibility(View.VISIBLE);

            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.GONE);
            linearLayout3.setVisibility(View.GONE);
            linearLayout4.setVisibility(View.GONE);

            textLeaderboard.setVisibility(View.GONE);
            buttonBack.setVisibility(View.GONE);
        }
    }

    public String chooseOne(double number) {
        String theName = "";
        if (number == pl1Time) {
            theName = pl1Name;
        } else if (number == pl2Time) {
            theName = pl2Name;
        } else if (number == pl3Time) {
            theName = pl3Name;
        } else if (number == pl4Time) {
            theName = pl4Name;
        } else {
            theName = "None";
        }

        return theName;
    }
}