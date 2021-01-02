package com.e.androidpuzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

public class WaitingRoomActivity extends AppCompatActivity {

    private TextView hostText;
    private TextView pl1Text;
    private TextView pl2Text;
    private TextView pl3Text;

    String currentTableName;
    String currentNickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.waiting_room_layout);


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