package com.example.djattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    DatabaseAccess databaseAccess;

    private final String USER_DDB_TABLE = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.textViewLogin);

        buttonRegister.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

        InitDbAsyncTask initDb = new InitDbAsyncTask();
        initDb.execute();
    }
    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            // email is empty
            Toast.makeText(this,"Please enter Email Address", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(password)){
            // password is empty
            Toast.makeText(this,"Please enter Password", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        //if proper then register :
        progressDialog.setMessage("Creating your account...");
        progressDialog.show();

        CreateItemAsyncTask createItemAsyncTask = new CreateItemAsyncTask();
        Document user = new Document();
        user.put("email", email);
        user.put("password", password);
        createItemAsyncTask.execute(user);
    }

    private void loginUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            // email is empty
            Toast.makeText(this,"Please enter Email Address", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        if(TextUtils.isEmpty(password)){
            // password is empty
            Toast.makeText(this,"Please enter Password", Toast.LENGTH_SHORT).show();
            return; //stop function from executing further
        }
        progressDialog.setMessage("Trying to Log you in...");
        progressDialog.show();


        GetItemAsyncTask getItemAsyncTask = new GetItemAsyncTask();
        getItemAsyncTask.execute(email);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonRegister){
            registerUser();
        }
        if(view == buttonLogin){
            loginUser();
        }
    }

    private class InitDbAsyncTask extends AsyncTask<Document, Void, Void> {
        @Override
        protected Void doInBackground(Document... documents) {
            databaseAccess = new DatabaseAccess();
            databaseAccess.init(MainActivity.this);
            return null;
        }

        protected void onPostExecute(Void v) {
            Log.d("POST EXECUTE", "InitDbAsyncTask completed");
        }
    }

    private class CreateItemAsyncTask extends AsyncTask<Document, Void, String> {
        @Override
        protected String doInBackground(Document... documents) {
            String email = editTextEmail.getText().toString().trim();

            Document doc = databaseAccess.getDocByPrimaryKey(email, USER_DDB_TABLE);
            if (doc == null) {
                databaseAccess.create(documents[0], USER_DDB_TABLE);
                return "Account created successfully";
            }
            else {
                return "Email already exists! Try another email.";
            }
        }

        protected void onPostExecute(String message) {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this,message, Toast.LENGTH_SHORT).show();
            if(message.startsWith("Email already exists")) {
                return;
            }
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            Log.d("POST EXECUTE", "CreateItemAsyncTask completed");
        }
    }

    private class GetItemAsyncTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... username) {
            return databaseAccess.getDocByPrimaryKey(username[0], USER_DDB_TABLE);
        }

        protected void onPostExecute(Document document) {
            progressDialog.dismiss();
            if(document == null){
                Toast.makeText(MainActivity.this,"Username is not registered! " +
                        "Please register", Toast.LENGTH_SHORT).show();
            }
            else{
                String password = editTextPassword.getText().toString().trim();
                try{
                    System.out.println("Get Result" + document.get("password").asString());
                    if(document.get("password").asString().equals(password)) {
                        finish();
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Password Incorrect! " +
                                "Please try again", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception exception) {
                    Toast.makeText(MainActivity.this,"Password Incorrect",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

}

