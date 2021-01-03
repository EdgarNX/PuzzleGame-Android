package com.e.androidpuzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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


    public String currentTableName;

    private double pl1Time = 0;
    private double pl2Time = 0;
    private double pl3Time = 0;
    private double pl4Time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_layout);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            currentTableName = bundle.getString(WaitingRoomActivity.ROOM_NAME_MESSAGE_KEY);
        }

        initialize();

        //backOrStop();

        theAliveQuery();
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

                                if(!object.get("plOneTime").toString().equals("no")) {
                                    pl1Time = Double.parseDouble(object.get("plOneTime").toString());
                                }

                                if(!object.get("plTwoTime").toString().equals("no")) {
                                    pl2Time = Double.parseDouble(object.get("plTwoTime").toString());
                                }

                                if(!object.get("plThreeTime").toString().equals("no")) {
                                    pl3Time = Double.parseDouble(object.get("plThreeTime").toString());
                                }

                                if(!object.get("plFourTime").toString().equals("no")) {
                                    pl4Time = Double.parseDouble(object.get("plFourTime").toString());
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
        if(pl1Time != 0 && pl2Time != 0 &&  pl3Time != 0 &&  pl4Time != 0) {
            progressBar.setVisibility(View.GONE);

//            if(pl1Time == 99999999) {
//                linearLayout1.setVisibility(View.GONE);
//            }
//            if(pl2Time == 99999999) {
//                linearLayout1.setVisibility(View.GONE);
//            }
//            if(pl3Time == 99999999) {
//                linearLayout1.setVisibility(View.GONE);
//            }
//            if(pl4Time == 99999999) {
//                linearLayout1.setVisibility(View.GONE);
//            }


        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

}