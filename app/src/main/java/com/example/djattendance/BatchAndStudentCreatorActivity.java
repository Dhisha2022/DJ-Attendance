package com.example.djattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class BatchAndStudentCreatorActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView viewAddStudent, viewAddBatch, viewViewStudents, viewViewBatch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_and_student_creator);

        viewViewBatch = (ImageView) findViewById(R.id.viewRooms);
        viewViewStudents = (ImageView) findViewById(R.id.viewStudents);
        viewAddBatch = (ImageView) findViewById(R.id.addRoom);
        viewAddStudent = (ImageView) findViewById(R.id.addStudent);

        viewViewBatch.setOnClickListener(this);
        viewViewStudents.setOnClickListener(this);
        viewAddBatch.setOnClickListener(this);
        viewAddStudent.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == viewAddBatch){
            startActivity(new Intent(getApplicationContext(),CreateBatchActivity.class));
        }
        if(view == viewAddStudent){
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }
        if(view == viewViewBatch){
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }
        if(view == viewViewStudents){
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }
    }


}
