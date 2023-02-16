package com.example.djattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.util.Calendar;
import java.util.UUID;

public class AddRoomActivity extends AppCompatActivity implements Button.OnClickListener{

    DatabaseAccess addRoomDatabaseAccess;
    private final String ROOM_DDB_TABLE = "Room";
    private Button buttonSubmit;
    private ProgressDialog progressDialog;
    private EditText name, capacity, availability, rent;
    private CheckBox isTable, isChair, isFan, isWardrobe;

    private String branchVal;
    private RadioGroup branchRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        progressDialog = new ProgressDialog(this);
        name = (EditText) findViewById(R.id.room_name);
        capacity = (EditText) findViewById(R.id.capacity);
        availability = (EditText) findViewById(R.id.beds_available);
        rent = (EditText) findViewById(R.id.spot_rent);
        branchRadioGroup = (RadioGroup) findViewById(R.id.BatchGroup);

        isTable = (CheckBox) findViewById(R.id.table);
        isChair = (CheckBox) findViewById(R.id.chair);
        isFan = (CheckBox) findViewById(R.id.fan);
        isWardrobe = (CheckBox) findViewById(R.id.wardrobe);

        buttonSubmit = (Button) findViewById(R.id.submit_room);
        buttonSubmit.setOnClickListener(this);

        InitDbAsyncTask initDb = new InitDbAsyncTask();
        initDb.execute();
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSubmit){
            addRoom();
        }
    }

    private void addRoom() {
        Log.d("Add Room", "Adding Room");
        String nameVal = name.getText().toString().trim();
        String capacityVal = capacity.getText().toString().trim();
        String availabilityVal = availability.getText().toString().trim();
        String rentVal = rent.getText().toString().trim();
        boolean isChairVal = isChair.isChecked();
        boolean isTableVal = isTable.isChecked();
        boolean isFanVal = isFan.isChecked();
        boolean isWardrobeVal = isWardrobe.isChecked();

        int radioButtonID = branchRadioGroup.getCheckedRadioButtonId();
        View radioButton = branchRadioGroup.findViewById(radioButtonID);
        int idx = branchRadioGroup.indexOfChild(radioButton);
        RadioButton r = (RadioButton) branchRadioGroup.getChildAt(idx);
        branchVal = r.getText().toString();

        if(TextUtils.isEmpty(nameVal)){
            Toast.makeText(this,"Please enter Room Name", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(capacityVal)){
            Toast.makeText(this,"Please enter Room Capacity", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(availabilityVal)){
            Toast.makeText(this,"Please enter Beds Availability", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(rentVal)){
            Toast.makeText(this,"Please enter Rent", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(Integer.parseInt(capacityVal) < Integer.parseInt(availabilityVal)) {
            Toast.makeText(this,"Capacity shoule be more than availability", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }


        progressDialog.setMessage("Adding room...");
        progressDialog.show();

        CreateItemAsyncTask createItemAsyncTask = new CreateItemAsyncTask();
        Document user = new Document();
        user.put("room_name", nameVal);
        user.put("capacity", Integer.parseInt(capacityVal));
        user.put("availability", Integer.parseInt(availabilityVal));
        user.put("rent", rentVal);
        user.put("branch", branchVal);
        user.put("is_chair_available", isChairVal);
        user.put("is_fan_available", isFanVal);
        user.put("is_table_available", isTableVal);
        user.put("is_wardrobe_available", isWardrobeVal);
        createItemAsyncTask.execute(user);
    }

    private class InitDbAsyncTask extends AsyncTask<Document, Void, Void> {
        @Override
        protected Void doInBackground(Document... documents) {
            addRoomDatabaseAccess = new DatabaseAccess();
            addRoomDatabaseAccess.init(AddRoomActivity.this);
            return null;
        }

        protected void onPostExecute(Void v) {
            Log.d("POST EXECUTE", "InitDbAsyncTask completed");
        }
    }

    private class CreateItemAsyncTask extends AsyncTask<Document, Void, String> {
        @Override
        protected String doInBackground(Document... documents) {
            addRoomDatabaseAccess.create(documents[0], ROOM_DDB_TABLE);
            return "Room added successfully";
        }

        protected void onPostExecute(String val) {
            progressDialog.dismiss();
            Toast.makeText(AddRoomActivity.this,val, Toast.LENGTH_SHORT).show();
            name.setText("");
            rent.setText("");
            capacity.setText("");
            availability.setText("");
            isChair.setChecked(false);
            isTable.setChecked(false);
            isWardrobe.setChecked(false);
            isFan.setChecked(false);
            branchRadioGroup.check(R.id.csBatch);

        }
    }
}