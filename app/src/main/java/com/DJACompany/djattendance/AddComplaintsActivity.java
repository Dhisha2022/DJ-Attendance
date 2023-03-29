package com.DJACompany.djattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddComplaintsActivity extends AppCompatActivity implements Button.OnClickListener {
    DatabaseAccess addComplaintsDatabaseAccess;
    private final String COMPLAINTS_DDB_TABLE = "Complaint";
    private Button buttonSubmit;
    private ProgressDialog progressDialog;
    private EditText name, complaintDate, complaint, fineAmount;
    private CheckBox warning, fine, finePaid;

    final Calendar complaintsCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaints);
        progressDialog = new ProgressDialog(this);
        name = (EditText) findViewById(R.id.complaint_student_name);
        complaintDate = (EditText) findViewById(R.id.date_of_complaint);
        complaint = (EditText) findViewById(R.id.complaint_details);
        fineAmount = (EditText) findViewById(R.id.fine_amount);
        warning = (CheckBox) findViewById(R.id.warning_checkbox);
        fine = (CheckBox) findViewById(R.id.fine_checkbox);
        finePaid = (CheckBox) findViewById(R.id.fine_paid);
        buttonSubmit = (Button) findViewById(R.id.submit_complaints);

        buttonSubmit.setOnClickListener(this);

        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fineAmount.setEnabled(false);
                fine.setChecked(false);
                finePaid.setChecked(false);
                finePaid.setEnabled(false);
            }
        });

        fine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fineAmount.setEnabled(true);
                finePaid.setEnabled(true);
                warning.setChecked(false);
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                complaintsCalendar.set(Calendar.YEAR, year);
                complaintsCalendar.set(Calendar.MONTH,month);
                complaintsCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        complaintDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddComplaintsActivity.this, date,
                        complaintsCalendar.get(Calendar.YEAR),
                        complaintsCalendar.get(Calendar.MONTH),
                        complaintsCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });


        InitDbAsyncTask initDb = new InitDbAsyncTask();
        initDb.execute();
    }
    private void updateLabel(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.UK);
        complaintDate.setText(dateFormat.format(complaintsCalendar.getTime()));
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSubmit){
            addStudentComplaint();
        }
    }

    private void addStudentComplaint() {
        Log.d("Add Complaints", "Adding complaints");
        String nameVal = name.getText().toString().trim();
        String complaintDateVal = complaintDate.getText().toString().trim();
        String complaintVal = complaint.getText().toString().trim();
        boolean isWarning = warning.isChecked();
        boolean isFine = fine.isChecked();
        boolean isFinePaid = finePaid.isChecked();
        float fineAmountVal = 0;

        if(TextUtils.isEmpty(nameVal)){
            Toast.makeText(this,"Please enter Name", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(complaintDateVal)){
            Toast.makeText(this,"Please enter Complaint Date", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(complaintVal)){
            Toast.makeText(this,"Please enter Complaint Details", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(isFine && TextUtils.isEmpty(fineAmount.getText().toString().trim())) {
            Toast.makeText(this,"Please enter fine Amount", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(isFine){
            fineAmountVal = Float.parseFloat(fineAmount.getText().toString().trim());
        }

        progressDialog.setMessage("Adding student complaint...");
        progressDialog.show();

        CreateItemAsyncTask createItemAsyncTask = new CreateItemAsyncTask();
        Document user = new Document();
        user.put("complaint_id",  UUID.randomUUID().toString());
        user.put("student_name", nameVal);
        user.put("complaint_details", complaintVal);
        user.put("complaint_date", complaintDateVal);
        user.put("is_warning", isWarning);
        user.put("is_fine", isFine);
        user.put("fine_amount", fineAmountVal);
        user.put("fine_paid", isFinePaid);
        createItemAsyncTask.execute(user);
    }

    private class InitDbAsyncTask extends AsyncTask<Document, Void, Void> {
        @Override
        protected Void doInBackground(Document... documents) {
            addComplaintsDatabaseAccess = new DatabaseAccess();
            addComplaintsDatabaseAccess.init(AddComplaintsActivity.this);
            return null;
        }

        protected void onPostExecute(Void v) {
            Log.d("POST EXECUTE", "InitDbAsyncTask completed");
        }
    }

    private class CreateItemAsyncTask extends AsyncTask<Document, Void, String> {
        @Override
        protected String doInBackground(Document... documents) {
            addComplaintsDatabaseAccess.create(documents[0], COMPLAINTS_DDB_TABLE);
            return "Complaint added successfully";
        }

        protected void onPostExecute(String val) {
            progressDialog.dismiss();
            Toast.makeText(AddComplaintsActivity.this,val, Toast.LENGTH_SHORT).show();
            name.setText("");
            complaint.setText("");
            complaintDate.setText("");
            fineAmount.setText("");
            fine.setChecked(false);
            warning.setChecked(false);
            finePaid.setChecked(false);
            finePaid.setEnabled(false);
        }
    }

}