package com.DJACompany.djattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddStudent extends AppCompatActivity implements Button.OnClickListener {
    DatabaseAccess addStudentDatabaseAccess;
    private final String STUDENT_DDB_TABLE = "Student";
    private final String ROOM_DDB_TABLE = "Room";

    private ArrayList<String> roomNameArrayList = new ArrayList<>();
    HashMap<String, Room> roomMap = new HashMap<String, Room>();

    private Button buttonSubmit;
    private ProgressDialog progressDialog;
    private EditText name, parent, phone, address, dob, parentPhone, email;

    private String branchVal;
    private RadioGroup branchRadioGroup;
    private Spinner roomListSpinner;
    final Calendar myCalendar= Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        progressDialog = new ProgressDialog(this);
        buttonSubmit =  (Button) findViewById(R.id.submit_visitor);
        name = (EditText) findViewById(R.id.name);
        parent = (EditText) findViewById(R.id.parent_name);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);
        dob = (EditText) findViewById(R.id.dob);
        parentPhone = (EditText) findViewById(R.id.parent_phone);
        email = (EditText) findViewById(R.id.student_email);
        branchRadioGroup = (RadioGroup) findViewById(R.id.radioGroupBatch);
        buttonSubmit.setOnClickListener(this);
        roomListSpinner = (Spinner) findViewById(R.id.RoomSpinner);

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

        branchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                View radioButton = branchRadioGroup.findViewById(checkedId);
                int idx = branchRadioGroup.indexOfChild(radioButton);
                RadioButton r = (RadioButton) branchRadioGroup.getChildAt(idx);
                branchVal = r.getText().toString();
                Log.d("branchVal", branchVal);

                progressDialog.setMessage("Refreshing Room List...");
                progressDialog.show();
                roomNameArrayList.clear();
                for (Map.Entry<String, Room> set : roomMap.entrySet()) {
                    if(branchVal.equals(set.getValue().getRoomBranch())){
                        String room_name = set.getValue().getRoomName();
                        roomNameArrayList.add(room_name);
                    }
                }
                Log.d("roomNameArrayList", roomNameArrayList.toString());
                ArrayAdapter adapter= new ArrayAdapter(
                        getApplicationContext(), android.R.layout.simple_spinner_item, roomNameArrayList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                roomListSpinner.setAdapter(adapter);
                progressDialog.dismiss();

            }
        });


        InitDbAsyncTask initDb = new InitDbAsyncTask();
        initDb.execute();

        GetItemAsyncTask getItemAsyncTask = new GetItemAsyncTask();
        getItemAsyncTask.execute();
        progressDialog.setMessage("Finding Rooms...");
        progressDialog.show();
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
        String parentPhoneVal = parentPhone.getText().toString().trim();
        String addressVal = address.getText().toString().trim();
        String dobVal = dob.getText().toString().trim();
        String emailVal = email.getText().toString().trim();

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
        if(TextUtils.isEmpty(dobVal)){
            // password is empty
            Toast.makeText(this,"Please enter DOB", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(parentVal)){
            Toast.makeText(this,"Please enter Parent Name", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(parentPhoneVal)){
            Toast.makeText(this,"Please enter Parent Phone", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(phoneVal)) {
            Toast.makeText(this,"Please enter Phone", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(phoneVal.length() != 10 || parentPhoneVal.length() != 10){
            Toast.makeText(this,"Please add valid 10 digit Phone number", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(addressVal)){
            Toast.makeText(this,"Please enter Address Value", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(emailVal)){
            Toast.makeText(this,"Please enter email id", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        //if proper then register :
        progressDialog.setMessage("Adding student...");
        progressDialog.show();

        CreateItemAsyncTask createItemAsyncTask = new CreateItemAsyncTask();
        Document user = new Document();
        user.put("name", nameVal);
        user.put("parent", parentVal);
        user.put("parent_phone", parentPhoneVal);
        user.put("phone", phoneVal);
        user.put("address", addressVal);
        user.put("dob", dobVal);
        user.put("branch", branchVal);
        user.put("email", emailVal);
        user.put("room", roomListSpinner.getSelectedItem().toString());
        createItemAsyncTask.execute(user);
    }

    private class CreateItemAsyncTask extends AsyncTask<Document, Void, String> {
        @Override
        protected String doInBackground(Document... documents) {
            String nameVal = name.getText().toString().trim();
//            addStudentDatabaseAccess.create(documents[0], STUDENT_DDB_TABLE);
            Document doc = addStudentDatabaseAccess.getDocByPrimaryKey(nameVal, STUDENT_DDB_TABLE);
            if (doc == null) {
                String roomName = roomListSpinner.getSelectedItem().toString();
                Room r = roomMap.get(roomName);
                Document room = new Document();
                room.put("room_name", r.getRoomName());
                room.put("capacity", r.getCapacity());
                room.put("availability", r.getavailability()-1);
                room.put("rent", r.getRent());
                room.put("branch", branchVal);
                room.put("is_chair_available", r.get_chair());
                room.put("is_fan_available", r.get_fan());
                room.put("is_table_available", r.get_table());
                room.put("is_wardrobe_available", r.get_wardbrobe());
                Document d = addStudentDatabaseAccess.addStudent(documents[0], STUDENT_DDB_TABLE, ROOM_DDB_TABLE, room );
                Log.d("doc val", d.toString());
                return "Student added successfully! " +
                        "Availability of room "+roomName+" has been updated to "+ (roomMap.get(roomName).getavailability()-1);
            }
            else {
                return "Student already exists";
            }
        }

        protected void onPostExecute(String val) {
            progressDialog.dismiss();
            Toast.makeText(AddStudent.this,val, Toast.LENGTH_LONG).show();
            if(val.equals("Student already exists")) {}
            else {
                finish();
            }
        }
    }

    private class GetItemAsyncTask extends AsyncTask<Void, Void, List<Document>> {
        @Override
        protected List<Document> doInBackground(Void... voids) {
            return addStudentDatabaseAccess.scanTableForAvailableRooms(ROOM_DDB_TABLE);
        }

        protected void onPostExecute(List<Document> documents) {
            int radioButtonID = branchRadioGroup.getCheckedRadioButtonId();
            View radioButton = branchRadioGroup.findViewById(radioButtonID);
            int idx = branchRadioGroup.indexOfChild(radioButton);
            RadioButton r = (RadioButton) branchRadioGroup.getChildAt(idx);
            branchVal = r.getText().toString();

            roomNameArrayList.clear();
            for (Document element: documents) {
                String room_name = element.get("room_name").asString();
                String room_branch = element.get("branch").asString();
                int availability = element.get("availability").asNumber().intValue();
                int capacity = element.get("capacity").asNumber().intValue();
                String branch = element.get("branch").asString();
                Log.d("RollCallActivity", room_name);
                Room room = new Room();
                room.setCapacity(capacity);
                room.setAvailability(availability);
                room.setRoomName(room_name);
                room.setRoomBranch(room_branch);
                room.setIs_chair( element.get("is_chair_available").asBoolean());
                room.setIs_fan( element.get("is_fan_available").asBoolean());
                room.setIs_table( element.get("is_table_available").asBoolean());
                room.setIs_wardbrobe( element.get("is_wardrobe_available").asBoolean());
                room.setRent( element.get("rent").asString());
                if(branch.equals(branchVal)){
                    roomNameArrayList.add(room_name);
                }
                roomMap.put(room_name, room);
            }
            Log.d("Room List", roomNameArrayList.toString());
            Log.d("Room Map", roomMap.toString());

            ArrayAdapter adapter= new ArrayAdapter(
                    getApplicationContext(), android.R.layout.simple_spinner_item, roomNameArrayList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            roomListSpinner.setAdapter(adapter);
            progressDialog.dismiss();

            Log.d("Selected Item", roomListSpinner.getSelectedItem().toString());
        }
    }

}
