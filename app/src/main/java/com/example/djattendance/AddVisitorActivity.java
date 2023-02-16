package com.example.djattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddVisitorActivity extends AppCompatActivity implements Button.OnClickListener{

    DatabaseAccess addVisitorDatabaseAccess;
    private final String VISITOR_DDB_TABLE = "Visitor";
    private Button buttonSubmit;
    private ProgressDialog progressDialog;
    private EditText name, phone, visitorName, visitorAddress, dov, purpose, arrival, studentRelation;

    final Calendar visitorCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visitor);
        progressDialog = new ProgressDialog(this);
        buttonSubmit =  (Button) findViewById(R.id.submit_visitor);
        visitorName = (EditText) findViewById(R.id.visitor_name);
        name = (EditText) findViewById(R.id.student_name_for_visitor);
        phone = (EditText) findViewById(R.id.visitor_phone);
        visitorAddress = (EditText) findViewById(R.id.visitor_address);
        dov = (EditText) findViewById(R.id.date_of_visit);
        purpose = (EditText) findViewById(R.id.purpose_of_visit);
        studentRelation = (EditText) findViewById(R.id.relation);
        buttonSubmit.setOnClickListener(this);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                visitorCalendar.set(Calendar.YEAR, year);
                visitorCalendar.set(Calendar.MONTH,month);
                visitorCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        dov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddVisitorActivity.this, date,
                        visitorCalendar.get(Calendar.YEAR),
                        visitorCalendar.get(Calendar.MONTH),
                        visitorCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        InitDbAsyncTask initDb = new InitDbAsyncTask();
        initDb.execute();

    }
    private void updateLabel(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.UK);
        dov.setText(dateFormat.format(visitorCalendar.getTime()));
    }


    @Override
    public void onClick(View view) {
        if(view == buttonSubmit){
            addVisitor();
        }
    }

    private void addVisitor() {
        Log.d("Add Visitor", "Adding Visitor");
        String nameVal = name.getText().toString().trim();
        String visitorNameVal = visitorName.getText().toString().trim();
        String dovVal = dov.getText().toString().trim();
        String phoneVal = phone.getText().toString().trim();
        String visitorAddressVal = visitorAddress.getText().toString().trim();
        String purposeVal = purpose.getText().toString().trim();
        String studentRelationVal = studentRelation.getText().toString().trim();

        if(TextUtils.isEmpty(visitorNameVal)){
            Toast.makeText(this,"Please enter Visitor Name", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(dovVal)){
            Toast.makeText(this,"Please enter Date of visit", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(nameVal)){
            Toast.makeText(this,"Please enter Student Name", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(phoneVal)){
            Toast.makeText(this,"Please enter Visitor Phone", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(visitorAddressVal)){
            Toast.makeText(this,"Please enter Visitor Address", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(purposeVal)){
            Toast.makeText(this,"Please enter Purpose of Visit", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(studentRelationVal)){
            Toast.makeText(this,"Please enter Student Relation", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }

        progressDialog.setMessage("Adding visitor...");
        progressDialog.show();

        CreateItemAsyncTask createItemAsyncTask = new CreateItemAsyncTask();
        Document user = new Document();
        user.put("visitor_id",  UUID.randomUUID().toString());
        user.put("visitor_name", visitorNameVal);
        user.put("student_name", nameVal);
        user.put("date_of_visit", dovVal);
        user.put("visitor_phone", phoneVal);
        user.put("visitor_address", visitorAddressVal);
        user.put("purpose_of_visit", purposeVal);
        user.put("relation", studentRelationVal);
        createItemAsyncTask.execute(user);
    }

    private class InitDbAsyncTask extends AsyncTask<Document, Void, Void> {
        @Override
        protected Void doInBackground(Document... documents) {
            addVisitorDatabaseAccess = new DatabaseAccess();
            addVisitorDatabaseAccess.init(AddVisitorActivity.this);
            return null;
        }

        protected void onPostExecute(Void v) {
            Log.d("POST EXECUTE", "InitDbAsyncTask completed");
        }
    }

    private class CreateItemAsyncTask extends AsyncTask<Document, Void, String> {
        @Override
        protected String doInBackground(Document... documents) {
            addVisitorDatabaseAccess.create(documents[0], VISITOR_DDB_TABLE);
            return "Visitor added successfully";
        }

        protected void onPostExecute(String val) {
            progressDialog.dismiss();
            Toast.makeText(AddVisitorActivity.this,val, Toast.LENGTH_SHORT).show();
            visitorName.setText("");
            name.setText("");
            dov.setText("");
            visitorAddress.setText("");
            phone.setText("");
            purpose.setText("");
            studentRelation.setText("");
        }
    }

}