package com.example.djattendance;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Expression;
import com.amazonaws.mobileconnectors.dynamodbv2.document.PutItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanFilter;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseAccess {
    private final String COGNITO_IDENTITY_POOL_ID = "ap-south-1:1e963e65-ce07-46a5-865e-4d4011e28687";
    //"us-west-2:98dadbe5-9129-4bea-a032-a99334ba5516";
    private final Regions COGNITO_IDENTITY_REGION = Regions.AP_SOUTH_1;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient dbClient;

    public void init(Context context) {
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context, COGNITO_IDENTITY_POOL_ID, COGNITO_IDENTITY_REGION);
        if(credentialsProvider.getCachedIdentityId() == null) {
            credentialsProvider.refresh();
        }
        // Create a connection to DynamoDB
        dbClient = new AmazonDynamoDBClient(credentialsProvider);

        // Create a table reference
        dbClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
    }
    public Document create(Document memo, String table) {
        Table dbTable = Table.loadTable(dbClient, table);
        PutItemOperationConfig config = new PutItemOperationConfig();
        config.setReturnValue(ReturnValue.ALL_OLD);
        return dbTable.putItem(memo, config);
    }

    /**
     * Update an existing memo in the database
     * @param memo the memo to save
     */
    public Document update(Document memo, String table) {
        Table dbTable = Table.loadTable(dbClient, table);
        return dbTable.updateItem(memo, new UpdateItemOperationConfig().withReturnValues(ReturnValue.ALL_NEW));
    }

    /**
     * Delete an existing memo in the database
     * @param memo the memo to delete
     */
    public void delete(Document memo, String table) {
        Table dbTable = Table.loadTable(dbClient, table);
        dbTable.deleteItem(
                memo.get("userId").asPrimitive(),   // The Partition Key
                memo.get("noteId").asPrimitive());  // The Hash Key
    }

    public Document getDocByPrimaryKey(String username, String table) {
        Table dbTable = Table.loadTable(dbClient, table);
        return dbTable.getItem(new Primitive(username));
    }

    public List<Document> getAllMemos(String table) {
        Table dbTable = Table.loadTable(dbClient, table);
        return dbTable.query(new Primitive(credentialsProvider.getCachedIdentityId())).getAllResults();
    }

    public List<Document> scanTableForStudentsOfBranch(String table, String branch) {
        Table dbTable = Table.loadTable(dbClient, table);
        final Expression expression = new Expression();
        expression.setExpressionStatement("branch = :branch");
        expression.withExpressionAttibuteValues(":branch", new Primitive(branch));
        return dbTable.scan(expression).getAllResults();
    }

    public Void createAttendance(ArrayList<Model> selectedStudents, String batch, String date, String table, Boolean leave) {
        Table dbTable = Table.loadTable(dbClient, table);
        PutItemOperationConfig config = new PutItemOperationConfig();
        config.setReturnValue(ReturnValue.ALL_OLD);
        for(Model name: selectedStudents) {
            Document doc = new Document();
            String pk = batch + "_" + name.getPlayer()+"_"+date;
            doc.put("branch_student", pk);
            doc.put("date", date);
            doc.put("student", name.getPlayer());
            doc.put("branch", batch);
            if(leave) {
                if(name.getSelected()) {
                    doc.put("attendance", "on leave");
                }
                dbTable.putItem(doc, config);
            }
            else {
                if(name.getSelected()) {
                    doc.put("attendance", "present");
                }
                else{
                    doc.put("attendance", "absent");
                }
                dbTable.putItem(doc, config);
            }

        }
        return null;
    }


}
