package com.example.djattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ViewStudents extends AppCompatActivity {
    ProgressDialog mProgressBar;
    DatabaseAccess viewStudentAttendanceDatabaseAccess;
    private final String ATTENDANCE_DDB_TABLE = "Attendance";
    TableLayout tableLayout;
    private ProgressDialog progressDialog;
    Resources resource;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_students);
        LayoutInflater inflater = getLayoutInflater();
        mProgressBar = new ProgressDialog(this);
        progressDialog = new ProgressDialog(this);
        tableLayout = findViewById(R.id.main_table);
        resource = getApplicationContext().getResources();

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView dateView = new TextView(this);
        TextView attendanceView = new TextView(this);
        TextView space = new TextView(getApplicationContext());
        space.setTextColor(Color.BLACK);
        space.setGravity(Gravity.CENTER);
        space.setText("   ");
        space.setTextSize(25);

        dateView.setText("Date                      ");
        attendanceView.setText("Attendance");
        dateView.setTextColor(Color.BLACK);
        dateView.setGravity(Gravity.LEFT);
        dateView.setTextSize(25);

        attendanceView.setTextColor(Color.BLACK);
        attendanceView.setGravity(Gravity.CENTER);
        attendanceView.setTextSize(25);

        row.addView(dateView);
        row.addView(space);
        row.addView(attendanceView);
        row.setPadding(10, 20, 10, 20);
        row.setLayoutParams(new
                TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
        row.setBackgroundColor(resource.getColor(R.color.purple_200));
        tableLayout.addView(row,0);

        userName = ((GlobalVariable) this.getApplication()).getUserName();
        Log.d("Dhisha", userName);
        InitDbAsyncTask initDb = new InitDbAsyncTask();
        initDb.execute();

        GetItemAsyncTask getItemAsyncTask = new GetItemAsyncTask();
        getItemAsyncTask.execute();
        progressDialog.setMessage("Loading Attendance...");
        progressDialog.show();

    }

    private class InitDbAsyncTask extends AsyncTask<Document, Void, Void> {
        @Override
        protected Void doInBackground(Document... documents) {
            viewStudentAttendanceDatabaseAccess = new DatabaseAccess();
            viewStudentAttendanceDatabaseAccess.init(ViewStudents.this);
            return null;
        }

        protected void onPostExecute(Void v) {
            Log.d("POST EXECUTE", "InitDbAsyncTask completed");
        }
    }


    private class GetItemAsyncTask extends AsyncTask<Void, Void, List<Document>> {
        @Override
        protected List<Document> doInBackground(Void... voids) {
            return viewStudentAttendanceDatabaseAccess.getAttendance(ATTENDANCE_DDB_TABLE, userName);
        }

        protected void onPostExecute(List<Document> documents) {
            int index = 1;
            for (Document d: documents) {
                String date = d.get("date").asString();
                String presentOrAbsent = d.get("attendance").asString();
                TableRow row= new TableRow(getApplicationContext());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                TextView dateView = new TextView(getApplicationContext());
                TextView attendanceView = new TextView(getApplicationContext());
                dateView.setText(date);
                attendanceView.setText(presentOrAbsent);

                dateView.setTextColor(Color.BLACK);
                dateView.setGravity(Gravity.LEFT);
                dateView.setTextSize(25);

                TextView space = new TextView(getApplicationContext());
                space.setTextColor(Color.BLACK);
                space.setGravity(Gravity.CENTER);
                space.setText("   ");
                space.setTextSize(25);

                attendanceView.setTextColor(Color.BLACK);
                attendanceView.setGravity(Gravity.RIGHT);
                attendanceView.setTextSize(25);

                row.addView(dateView);
                row.addView(space);
                row.addView(attendanceView);
                if(index % 2 == 0){
                    row.setBackgroundColor(resource.getColor(R.color.grey_100));
                }
                row.setPadding(10, 20, 10, 20);
                tableLayout.addView(row,index);
                index ++;
            }
            progressDialog.dismiss();
        }
    }
}