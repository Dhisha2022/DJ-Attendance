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
    private LinearLayout buttonEnrollBatch, buttonChatbot, buttonRollcall, buttonCheckAttendance;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        buttonChatbot = findViewById(R.id.linearLayoutChatbot);
        buttonEnrollBatch = findViewById(R.id.linearLayoutEnroll);
        buttonRollcall = findViewById(R.id.linearLayoutRollCall);
        buttonCheckAttendance = findViewById( R.id.linearLayoutCheckAttendance);

        buttonChatbot.setOnClickListener(this);
        buttonEnrollBatch.setOnClickListener(this);
        buttonRollcall.setOnClickListener(this);
        buttonCheckAttendance.setOnClickListener(this);
    }


    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }



    @Override
    public void onClick(View view) {

        if ( view == buttonChatbot){
            startActivity(new Intent(this,ChatBotActivity.class));
            //startActivity(new Intent(this,EmptyDatabase.class));
        }

        if ( view == buttonEnrollBatch){
            startActivity(new Intent(this, AddStudent.class));
            //startActivity(new Intent(this,RegisterClass.class));
        }

        if ( view == buttonRollcall){
            startActivity(new Intent(this,RollcallActivity.class));
            //startActivity(new Intent(this,AttendanceMiddleware.class));
        }

        if ( view == buttonCheckAttendance ){
            startActivity(new Intent(this,CheckAttendanceActivity.class));
            //startActivity(new Intent(this, CheckAttendance.class));
        }


    }
}
