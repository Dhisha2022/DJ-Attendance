package com.example.djattendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

public class EnrollBatchActivity extends AppCompatActivity {

    private EditText editTextClassName, editTextStudentName;
    private Button buttonAddStudent, buttonRegisterClass;
    private ListView listViewSubjects;
    ArrayList<String> arrayListStudents;
    ArrayAdapter<String> arrayAdapterSubjects;
    String batchName, studentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_batch);

        editTextClassName = findViewById(R.id.editTextClassName);
        buttonAddStudent = findViewById(R.id.buttonAddSubject);
        buttonRegisterClass = findViewById(R.id.buttonRegisterClass);
        listViewSubjects = findViewById(R.id.listViewSubjects);
        editTextStudentName = findViewById(R.id.editTextSubjectName);
        arrayListStudents = new ArrayList<>();

//
//        firebaseAuth= FirebaseAuth.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        if(firebaseAuth.getCurrentUser() == null){
//            //user is not logged in
//            finish();
//            startActivity(new Intent(this,LoginActivity.class));
//        }

        buttonAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batchName = editTextClassName.getText().toString();
                studentName = editTextStudentName.getText().toString();
                if(batchName.length() == 0){
                    Toast.makeText(EnrollBatchActivity.this,"Please enter the " +
                            "batch name first", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(arrayListStudents.size() >=10){
                    Toast.makeText(EnrollBatchActivity.this,"Only 10 students " +
                            "or less allowed in a batch", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(arrayListStudents.contains(studentName)){
                    Toast.makeText(EnrollBatchActivity.this,"Student " +studentName+
                            "already exists. Please enter a different name", Toast.LENGTH_SHORT).show();
                    return;
                }
                arrayListStudents.add(studentName);

                arrayAdapterSubjects = new ArrayAdapter<String>(EnrollBatchActivity.this,
                        android.R.layout.simple_list_item_1, arrayListStudents);
                listViewSubjects.setAdapter(arrayAdapterSubjects);

            }
        });

        buttonRegisterClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batchName = editTextClassName.getText().toString();

                if(batchName.length() ==0 || arrayListStudents.size()==0){
                    Toast.makeText(EnrollBatchActivity.this,"Please enter students " +
                            "and Batch name", Toast.LENGTH_SHORT).show();
                    return;
                }

//                for (int i = 0; i < arrayListStudents.size(); i ++){
//
//                    Subject subject = new Subject(arrayListStudents.get(i), "0");
//                    databaseReference.child(className).child("Subjects").push().setValue(subject);
//                }

                Toast.makeText(EnrollBatchActivity.this,"Added Students to Batch",
                        Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });

    }


}
