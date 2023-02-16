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
    private LinearLayout buttonEnrollBatch, buttonChatbot, buttonRollcall, buttonCheckAttendance, buttonComplaints, buttonVistors;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        buttonChatbot = findViewById(R.id.linearLayoutChatbot);
        buttonEnrollBatch = findViewById(R.id.linearLayoutEnroll);
        buttonRollcall = findViewById(R.id.linearLayoutRollCall);
        buttonCheckAttendance = findViewById( R.id.linearLayoutCheckAttendance);
        buttonVistors = findViewById(R.id.linearLayoutAddVisitor);
        buttonComplaints = findViewById(R.id.linearLayoutAddComplaints);
        buttonChatbot.setOnClickListener(this);
        buttonEnrollBatch.setOnClickListener(this);
        buttonRollcall.setOnClickListener(this);
        buttonCheckAttendance.setOnClickListener(this);
        buttonVistors.setOnClickListener(this);
        buttonComplaints.setOnClickListener(this);
    }


    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }



    @Override
    public void onClick(View view) {

        if ( view == buttonChatbot){
            startActivity(new Intent(this,ChatBotActivity.class));
        }

        if ( view == buttonEnrollBatch){
            startActivity(new Intent(this, BatchAndStudentCreatorActivity.class));
        }

        if ( view == buttonRollcall){
            startActivity(new Intent(this, RollcallActivity.class));
        }

        if ( view == buttonCheckAttendance ){
            startActivity(new Intent(this, CheckAttendanceActivity.class));
        }

        if ( view == buttonVistors ){
            startActivity(new Intent(this, AddVisitorActivity.class));
        }

        if ( view == buttonComplaints ){
            startActivity(new Intent(this, AddComplaintsActivity.class));
        }

    }
}
