package com.example.myownwhatsappclone;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class IndividualChatListBars extends AppCompatActivity {

    private TextView txt;
    private String userName, properName;
    private EditText edtMessage;
    private Button btnSendMessage;
    private ListView listviewMessage;
    private ArrayList<String> dataChats;
    private ArrayAdapter arrayAdapter;
    //  ParseQuery<ParseObject> mainQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat_list_bars);
        edtMessage = findViewById(R.id.edtSendChatMsg);
        edtMessage.clearFocus();

        Intent receivedInfo = getIntent();
        userName = receivedInfo.getStringExtra("username");
        properName = receivedInfo.getStringExtra("proper_name");
        txt = findViewById(R.id.txt);
        txt.setText(properName);


        btnSendMessage = findViewById(R.id.btnSendChatMsg);

        edtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtMessage.setCursorVisible(true);
            }
        });
        listviewMessage = findViewById(R.id.listViewForIndividualChats);
        dataChats = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(
                getApplicationContext(), android.R.layout.simple_list_item_1, dataChats
        );






        listviewMessage.setAdapter(arrayAdapter);

        ParseQuery<ParseObject> senderMessages = new ParseQuery<ParseObject>("Message");
            senderMessages.whereEqualTo("sender_un", ParseUser.getCurrentUser().getUsername().toString());
            senderMessages.whereEqualTo("target_un", userName);

            ParseQuery<ParseObject> targetMessages = new ParseQuery<ParseObject>("Message");
            targetMessages.whereEqualTo("sender_un", userName);
            targetMessages.whereEqualTo("target_un", ParseUser.getCurrentUser().getUsername().toString());

            List<ParseQuery<ParseObject>> queries = new ArrayList
                    <ParseQuery<ParseObject>>();
            queries.add(senderMessages);
            queries.add(targetMessages);
            ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
            mainQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(objects.size()>0 && e==null){
                        for(ParseObject singleMessage:objects){
                            dataChats.add(singleMessage.get("sender_pn") + " : " + singleMessage.get("message"));
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                    else{
                        Toast.makeText(IndividualChatListBars.this, "No new messages!", Toast.LENGTH_SHORT).show();
                    }
                }
            });




        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("xJ0jtCZUmjvbz9AhghFgVklgWBO42Wn0pGVsWAYz")
                .clientKey("g62epomyT7TKbEvl7hLKBuXAEurj0KmvQmvybZ3f")
                .server("wss://myownwhatsappclone.back4app.io/").build()
        );
// Init Live Query Client
        ParseLiveQueryClient parseLiveQueryClient = null;

        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://myownwhatsappclone.back4app.io/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (parseLiveQueryClient != null) {

//            ParseQuery<ParseObject> senderMessages = new ParseQuery<ParseObject>("Message");
//            senderMessages.whereEqualTo("sender_un", ParseUser.getCurrentUser().getUsername().toString());
//            senderMessages.whereEqualTo("target_un", userName);

            ParseQuery<ParseObject> newMessages = new ParseQuery<ParseObject>("Message");
            targetMessages.whereEqualTo("sender_un", userName);
            targetMessages.whereEqualTo("target_un", ParseUser.getCurrentUser().getUsername().toString());

//            List<ParseQuery<ParseObject>> queries = new ArrayList
//                    <ParseQuery<ParseObject>>();
//            queries.add(senderMessages);
//            queries.add(targetMessages);
//            ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);



        SubscriptionHandling<ParseObject> subscriptionHandling = parseLiveQueryClient.subscribe(newMessages);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<ParseObject>() {
            @Override
            public void onEvent(ParseQuery<ParseObject> query, final ParseObject object) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {

                        dataChats.add(object.get("sender_pn") + " : " + object.get("message"));
                        arrayAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        }
    }

    public void sendMessage(View v){
        final String message = edtMessage.getText().toString();

        if(message.equals("")==false) {
            ParseObject obj = new ParseObject("Message");
            obj.put("sender_un", userName);
            obj.put("target_un", ParseUser.getCurrentUser().getUsername());
            obj.put("sender_pn", properName);
            obj.put("target_pn", ParseUser.getCurrentUser().get("name"));
            obj.put("message",message);

            obj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        dataChats.add(ParseUser.getCurrentUser().get("name").toString()+
                                " : " + message);

                       arrayAdapter.notifyDataSetChanged();
                        }

                    }

            });

            edtMessage.setText("");


        }

        else{
            Toast.makeText(this, "Empty Message!", Toast.LENGTH_SHORT).show();
        }
    }
}
