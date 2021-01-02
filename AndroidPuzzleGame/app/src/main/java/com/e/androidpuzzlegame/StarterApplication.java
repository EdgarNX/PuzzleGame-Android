package com.e.androidpuzzlegame;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;

import java.net.URI;
import java.net.URISyntaxException;

public class StarterApplication extends Application {

    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("rXppaOOXgQ0sN0ZmNqRZpJiqjBJGVpEqPFibq6IR")
                .clientKey("FFgIuXyN3RiMDt1RFoNT5WkmUan8N1b8JkjLhDfO")
                .server("https://parseapi.back4app.com")
                .build()
        );


        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
