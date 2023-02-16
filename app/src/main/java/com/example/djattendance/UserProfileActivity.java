package com.example.djattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    View buttonViewMyAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        buttonViewMyAttendance = (View)findViewById(R.id.linearLayoutViewMyAttendance);
        buttonViewMyAttendance.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        if ( view == buttonViewMyAttendance){
            startActivity(new Intent(this,ViewStudents.class));
        }
    }

}