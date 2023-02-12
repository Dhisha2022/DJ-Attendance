package com.example.djattendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout buttonEnrollBatch, buttonChatbot;
    private LinearLayout buttonRollcall, buttonCheckAttendance;

    private static final String TAG = "bluetooth1";

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "98:D3:33:80:A1:93";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        firebaseAuth= FirebaseAuth.getInstance();
        //buttonLogout = findViewById(R.id.buttonLogout);


        buttonChatbot = findViewById(R.id.linearLayoutChatbot);
        buttonEnrollBatch = findViewById(R.id.linearLayoutEnroll);
        buttonRollcall = findViewById(R.id.linearLayoutRollcall);
        buttonCheckAttendance = findViewById( R.id.linearLayoutCheckAttendance);

//        if(firebaseAuth.getCurrentUser() == null){
//            //user is not logged in
//            finish();
//            startActivity(new Intent(this,LoginActivity.class));
//        }
//
//        FirebaseUser user  = firebaseAuth.getCurrentUser();
        //buttonLogout.setOnClickListener(this);

//        checkBTState();



        buttonChatbot.setOnClickListener(this);
        buttonEnrollBatch.setOnClickListener(this);
        buttonRollcall.setOnClickListener(this);
        buttonCheckAttendance.setOnClickListener(this);
    }


    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");

//        try {
//            outStream.write(msgBuffer);
//        } catch (IOException e) {
//            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
//            if (address.equals("00:00:00:00:00:00"))
//                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
//            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
//
//            errorExit("Fatal Error", msg);
//        }
    }



    @Override
    public void onClick(View view) {

        if ( view == buttonChatbot){
            sendData("4");
            startActivity(new Intent(this,ChatBotActivity.class));
            //startActivity(new Intent(this,EmptyDatabase.class));
        }

        if ( view == buttonEnrollBatch){
            sendData("1");
            startActivity(new Intent(this, EnrollBatchActivity.class));
            //startActivity(new Intent(this,RegisterClass.class));
        }

        if ( view == buttonRollcall){
            sendData("2");
            startActivity(new Intent(this,RollcallActivity.class));
            //startActivity(new Intent(this,AttendanceMiddleware.class));
        }

        if ( view == buttonCheckAttendance ){
            startActivity(new Intent(this,CheckAttendanceActivity.class));
            //startActivity(new Intent(this, CheckAttendance.class));
        }


    }
}
