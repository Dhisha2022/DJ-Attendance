package com.example.djattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddStudent extends AppCompatActivity implements Button.OnClickListener {
    DatabaseAccess addStudentDatabaseAccess;
    private final String STUDENT_DDB_TABLE = "Student";
    private Button buttonSubmit;
    private ProgressDialog progressDialog;
    private EditText name, parent, phone, address, dob;

    private String branchVal;
    private RadioGroup branchRadioGroup;

    final Calendar myCalendar= Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        progressDialog = new ProgressDialog(this);
        buttonSubmit =  (Button) findViewById(R.id.submit);
        name = (EditText) findViewById(R.id.name);
        parent = (EditText) findViewById(R.id.parent_name);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);
        dob = (EditText) findViewById(R.id.dob);
        branchRadioGroup = (RadioGroup) findViewById(R.id.radioGroupBatch);
        buttonSubmit.setOnClickListener(this);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddStudent.this,date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        InitDbAsyncTask initDb = new InitDbAsyncTask();
        initDb.execute();
    }

    private void updateLabel(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.UK);
        dob.setText(dateFormat.format(myCalendar.getTime()));
    }


    @Override
    public void onClick(View view) {
        if(view == buttonSubmit){
            registerStudent();
        }
    }

    private class InitDbAsyncTask extends AsyncTask<Document, Void, Void> {
        @Override
        protected Void doInBackground(Document... documents) {
            addStudentDatabaseAccess = new DatabaseAccess();
            addStudentDatabaseAccess.init(AddStudent.this);
            return null;
        }

        protected void onPostExecute(Void v) {
            Log.d("POST EXECUTE", "InitDbAsyncTask completed");
        }
    }

    private void registerStudent(){
        Log.d("Add Student", "registerStudent");
        String nameVal = name.getText().toString().trim();
        String parentVal = parent.getText().toString().trim();
        String phoneVal = phone.getText().toString().trim();
        String addressVal = address.getText().toString().trim();
        String dobVal = dob.getText().toString().trim();

        int radioButtonID = branchRadioGroup.getCheckedRadioButtonId();
        View radioButton = branchRadioGroup.findViewById(radioButtonID);
        int idx = branchRadioGroup.indexOfChild(radioButton);
        RadioButton r = (RadioButton) branchRadioGroup.getChildAt(idx);
        branchVal = r.getText().toString();

        if(TextUtils.isEmpty(nameVal)){
            // email is empty
            Toast.makeText(this,"Please enter Name", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(parentVal)){
            // password is empty
            Toast.makeText(this,"Please enter Parent Name", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(phoneVal)) {
            // password is empty
            Toast.makeText(this,"Please enter Phone", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(phoneVal.length() != 10){
            // password is empty
            Toast.makeText(this,"Please add valid 10 digit Phone number", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(addressVal)){
            // password is empty
            Toast.makeText(this,"Please enter Address Value", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(dobVal)){
            // password is empty
            Toast.makeText(this,"Please enter DOB", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        //if proper then register :
        progressDialog.setMessage("Adding student...");
        progressDialog.show();

        CreateItemAsyncTask createItemAsyncTask = new CreateItemAsyncTask();
        Document user = new Document();
        user.put("name", nameVal);
        user.put("parent", parentVal);
        user.put("phone", phoneVal);
        user.put("address", addressVal);
        user.put("dob", dobVal);
        user.put("branch", branchVal);
        createItemAsyncTask.execute(user);
    }

    private class CreateItemAsyncTask extends AsyncTask<Document, Void, String> {
        @Override
        protected String doInBackground(Document... documents) {
            String nameVal = name.getText().toString().trim();
//            addStudentDatabaseAccess.create(documents[0], STUDENT_DDB_TABLE);
            Document doc = addStudentDatabaseAccess.getDocByPrimaryKey(nameVal, STUDENT_DDB_TABLE);
            if (doc == null) {
                addStudentDatabaseAccess.create(documents[0], STUDENT_DDB_TABLE);
                return "Student added successfully";
            }
            else {
                return "Student already exists";
            }
        }

        protected void onPostExecute(String val) {
            progressDialog.dismiss();
            Toast.makeText(AddStudent.this,val, Toast.LENGTH_SHORT).show();
            if(val.equals("Student already exists")) {
                return;
            }
            name.setText("");
            phone.setText("");
            parent.setText("");
            address.setText("");
            branchRadioGroup.check(R.id.cs);
            dob.setText("");
        }
    }
}
