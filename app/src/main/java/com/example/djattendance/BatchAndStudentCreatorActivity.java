package com.example.djattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BatchAndStudentCreatorActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout viewAddStudent, viewAddRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_and_student_creator);

        viewAddRoom = (LinearLayout) findViewById(R.id.linearLayoutAddRoom);
        viewAddStudent = (LinearLayout) findViewById(R.id.linearLayoutAddStudent);

        viewAddRoom.setOnClickListener(this);
        viewAddStudent.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == viewAddStudent){
            startActivity(new Intent(getApplicationContext(),AddStudent.class));
        }
        if(view == viewAddRoom){
            startActivity(new Intent(getApplicationContext(),AddRoomActivity.class));
        }
    }


}
