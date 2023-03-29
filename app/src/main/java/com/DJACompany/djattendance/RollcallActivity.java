package com.DJACompany.djattendance;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

public class RollcallActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Model> modelArrayList = new ArrayList<>();
    private CustomAdapter customAdapter;
    Button btnSelect, btnLeave;

    DatabaseAccess rollCallDatabaseAccess;

    private ProgressDialog progressDialog;
    private Spinner batchSelectSpinner;

    private final String STUDENT_DDB_TABLE = "Student";
    private final String ATTENDANCE_DDB_TABLE = "Attendance";
    public static String[] playerList = new String[]{"Sunil Chetri - INDIA",
            "Cristiano Ronaldo - Portugal",
            "Lionel Messi - Argentina",
            "Neymar Jr - Brazil",
            "Eden Hazard - Belgium",
            "Gigi Buffon - Italy",
            "James Rodrigues - Columbia",
            "Sadio Mane - Senegal",
            "Toni Kroos - Germany",
            "Toni Kroos - Germany"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rollcall);

        listView = findViewById(R.id.listView);
        btnSelect = findViewById(R.id.submitAttendance);
        btnLeave = findViewById(R.id.submitLeave);
        progressDialog = new ProgressDialog(this);

        batchSelectSpinner = findViewById(R.id.batchSelSpinner);

        batchSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(
                    AdapterView<?> adapterView, View view,
                    int i, long l) {
                progressDialog.setMessage("Fetching Students");
                progressDialog.show();
                GetItemAsyncTask getItemAsyncTask = new GetItemAsyncTask();
                Log.d("batchSelectSpinner Selection", batchSelectSpinner.getSelectedItem().toString());
                getItemAsyncTask.execute(batchSelectSpinner.getSelectedItem().toString());
            }

            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                progressDialog.setMessage("Marking Selected Names as present today");
                progressDialog.show();

                CreateItemAsyncTask createItemAsyncTask = new CreateItemAsyncTask();
                createItemAsyncTask.execute(false);
            }
        });

        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                progressDialog.setMessage("Marking Selected Names as on leave today");
                progressDialog.show();

                CreateItemAsyncTask createItemAsyncTask = new CreateItemAsyncTask();
                createItemAsyncTask.execute(true);
            }
        });

        progressDialog.setMessage("Fetching Students");
        progressDialog.show();
        InitDbAsyncTask initDb = new InitDbAsyncTask();
        initDb.execute();

        GetItemAsyncTask getItemAsyncTask = new GetItemAsyncTask();
        Log.d("batchSelectSpinner.getSelectedItem().toString()", batchSelectSpinner.getSelectedItem().toString());
        getItemAsyncTask.execute(batchSelectSpinner.getSelectedItem().toString());
    }

    private class InitDbAsyncTask extends AsyncTask<Document, Void, Void> {
        @Override
        protected Void doInBackground(Document... documents) {
            rollCallDatabaseAccess = new DatabaseAccess();
            rollCallDatabaseAccess.init(RollcallActivity.this);
            return null;
        }

        protected void onPostExecute(Void v) {
            Log.d("POST EXECUTE", "InitDbAsyncTask completed");
        }
    }

    private class GetItemAsyncTask extends AsyncTask<String, Void, List<Document>> {
        @Override
        protected List<Document> doInBackground(String... branch) {
            return rollCallDatabaseAccess.scanTableForStudentsOfBranch(STUDENT_DDB_TABLE, branch[0]);
        }

        protected void onPostExecute(List<Document> documents) {
            modelArrayList.clear();
            for (Document element: documents) {
                String studentName = element.get("name").asString();
                Log.d("RollCallActivity", studentName);
                Model model = new Model();
                model.setSelected(false);
                model.setPlayer(studentName);
                modelArrayList.add(model);
            }
            customAdapter = new CustomAdapter(RollcallActivity.this, modelArrayList);
            listView.setAdapter(customAdapter);
            progressDialog.dismiss();
        }
    }

    private class CreateItemAsyncTask extends AsyncTask<Boolean, Void, Document> {
        @Override
        protected Document doInBackground(Boolean... leave) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            String batchSelectedVal = batchSelectSpinner.getSelectedItem().toString();
            rollCallDatabaseAccess.createAttendance(
                modelArrayList, batchSelectedVal, currentDate, ATTENDANCE_DDB_TABLE, leave[0]
            );
            return null;
        }

        protected void onPostExecute(Document document) {
            Toast.makeText(getApplicationContext(), "Attendance Marked!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            Log.d("POST EXECUTE", "CreateItemAsyncTask completed");
        }
    }

}
