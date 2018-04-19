package com.example.rohilsheth.messagebot;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Object[] objects;
    SmsMessage[] messages;
    Boolean permissionTrue=false;
    SmsManager manager;
    String incoming = "";
    TextView display, state;
    String number;
    int currentState = 1;
    String outgoing = "";
    int stopTextingMe = 0;
    int temp;
    String[] possibleState = {"Intro","Begin Break-Up","Full Break Up","Explain","Leave","Confused"};
    String[] introPhrases = {"Yo!","What's good?","Hi!","Hey!"};
    String [] beginPhrases = {"I think we need to have a talk.","I've been thinking about something for a while","I've been having some thoughts about us.","We need to have a discussion."};
    String[] breakPhrases = {"I don't think we should see each other again.","We should not be together.","We need to break up.","This relationship is over."};
    String[] explainPhrases = {"This just is not working anymore.","You don't make me feel happy anymore.","I can't stand the sight of you.","This just isn't the same as it was."};
    String[] leavePhrases = {"I don't want to ever see you again.","Goodbye and do not contact me.","Bye. If you text me again I will file a restraining order on you.","Peace. I'm out."};
    String[] confusedPhrases = {"What?","That doesn't make sense.","What are you saying? I don't understand."};

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG","message");
        IntentFilter textFilter = new IntentFilter();
        textFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        BroadcastReceiver receiver = new TextReciever();
        registerReceiver(receiver,textFilter);
        manager = SmsManager.getDefault();
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = findViewById(R.id.ID_TextView);
        state = findViewById(R.id.textView);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 123);
        } else {
            Log.d("TAG", "Permission is granted");
        }
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECEIVE_SMS},123);
        }
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 123);
        }


    }
    public class TextReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            incoming = "";
            Log.d("TAG","recieved");
            Bundle recieverBundle = intent.getExtras();
            objects = (Object[]) recieverBundle.get("pdus");
            messages = new SmsMessage[objects.length];
            for(int i = 0;i<messages.length;i++) {
                messages[i] = SmsMessage.createFromPdu((byte[])objects[i],recieverBundle.getString("format"));
                Log.d("TAG1",messages[i].toString());
                incoming+=messages[i].getMessageBody();
            }
            display.setText(incoming);
            Log.d("TAG2",incoming);
            Log.d("TAG3",messages[0].getMessageBody().toLowerCase());
            number = messages[0].getOriginatingAddress();
            state.setText(currentState+" "+possibleState[currentState-1]);
            if((incoming.contains("bye")||incoming.contains("see you")||incoming.contains("goodbye"))&&(currentState<5)){
                temp = currentState;
                currentState = 6;
            }
            if (((incoming.contains("hello"))||incoming.contains("hi")||incoming.contains("what's up")||incoming.contains("hey"))&&(currentState>1)){
                temp = currentState;
                currentState = 6;
            }

            //determine outgoing
            if(currentState == 1) {
                outgoing = introPhrases[(int)(Math.random()*introPhrases.length)];
                Log.d("TAG",outgoing);
            }
            if(currentState == 2) {
                outgoing = beginPhrases[(int)(Math.random()*beginPhrases.length)];
            }
            if(currentState == 3) {
                outgoing = breakPhrases[(int)(Math.random()*breakPhrases.length)];
            }
            if(currentState == 4) {
                outgoing = explainPhrases[(int)(Math.random()*explainPhrases.length)];
            }
            if(currentState == 5) {
                outgoing = leavePhrases[(int)(Math.random()*leavePhrases.length)];
            }
            if(currentState == 6) {
                outgoing = confusedPhrases[(int)(Math.random()*confusedPhrases.length)];
            }
            //change state
            if(currentState == 1){
                currentState = 2;
            }
            else if (currentState == 2) {
                currentState = 3;
            }
            else if (currentState == 3) {
                currentState = 4;
            }
            else if (currentState == 4) {
                currentState = 5;
            }
            else if (currentState == 5) {
                stopTextingMe++;
                if(stopTextingMe>3){
                    outgoing="Please stop texting me. I will file charges.";
                }
            }
            else if (currentState == 6) {
                currentState = temp;
            }
            Log.d("TAG",outgoing);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    manager.sendTextMessage(number, null, outgoing, null, null);
                }
            },4000);


        }
    }
}


