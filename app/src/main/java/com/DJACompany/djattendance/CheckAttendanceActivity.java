package com.DJACompany.djattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CheckAttendanceActivity extends AppCompatActivity {

    private EditText name, dateText;
    private TextView absentPresent;
    private Spinner batchSelectSpinner;
    DatabaseAccess checkAttendanceDatabaseAccess;
    private final String ATTENDANCE_DDB_TABLE = "Attendance";
    final Calendar calendar= Calendar.getInstance();
    private ProgressDialog progressDialog;
    private Button checkAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        dateText = (EditText) findViewById(R.id.dateAttendance);
        name = (EditText) findViewById(R.id.StudentName);
        absentPresent = (TextView) findViewById(R.id.absentPresentText);
        batchSelectSpinner = findViewById(R.id.batchSpinner);
        checkAttendance = (Button)findViewById(R.id.checkAttendanceButton);
        progressDialog = new ProgressDialog(this);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CheckAttendanceActivity.this,date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        InitDbAsyncTask initDb = new InitDbAsyncTask();
        initDb.execute();

        checkAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String nameVal = name.getText().toString().trim();
                String dateVal = dateText.getText().toString().trim();
                if(TextUtils.isEmpty(nameVal)){
                    // password is empty
                    Toast.makeText(getApplicationContext(),"Please enter Student Name", Toast.LENGTH_SHORT).show();
                    return; //stop function from executing further
                }
                if(TextUtils.isEmpty(dateVal)){
                    // password is empty
                    Toast.makeText(getApplicationContext(),"Please enter Date", Toast.LENGTH_SHORT).show();
                    return; //stop function from executing further
                }

                progressDialog.setMessage("Checking attendance for " + nameVal +" on " + dateVal );
                progressDialog.show();

                String pk = batchSelectSpinner.getSelectedItem().toString()+"_"+nameVal+"_"+dateVal;
                Log.d("pk Value", pk);
                GetItemAsyncTask getItemAsyncTask = new GetItemAsyncTask();
                getItemAsyncTask.execute(pk);
            }
        });
    }

    private void updateLabel(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat);
        dateText.setText(dateFormat.format(calendar.getTime()));
    }

    private class InitDbAsyncTask extends AsyncTask<Document, Void, Void> {
        @Override
        protected Void doInBackground(Document... documents) {
            checkAttendanceDatabaseAccess = new DatabaseAccess();
            checkAttendanceDatabaseAccess.init(CheckAttendanceActivity.this);
            return null;
        }

        protected void onPostExecute(Void v) {
            Log.d("POST EXECUTE", "InitDbAsyncTask completed");
        }
    }

    private class GetItemAsyncTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... pk) {
            return checkAttendanceDatabaseAccess.getDocByPrimaryKey(pk[0], ATTENDANCE_DDB_TABLE);
        }

        protected void onPostExecute(Document document) {
            absentPresent.setText("");
            if(document == null) {
                absentPresent.setText("No Data found. Please check name and date. ");
            }
            else {
                String val = document.get("attendance").asString();
                absentPresent.setText("Student was "+val);
            }

            progressDialog.dismiss();
        }
    }

}