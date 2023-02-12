package com.example.djattendance;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.GetItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.PutItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.util.List;
import java.util.UUID;


public class DatabaseAccess {
    private final String COGNITO_IDENTITY_POOL_ID = "ap-south-1:1e963e65-ce07-46a5-865e-4d4011e28687";
    //"us-west-2:98dadbe5-9129-4bea-a032-a99334ba5516";
    private final Regions COGNITO_IDENTITY_REGION = Regions.AP_SOUTH_1;
    private final String DDB_TABLE = "User";
    private Context context;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;

    public void init(Context context1){
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context1, COGNITO_IDENTITY_POOL_ID, COGNITO_IDENTITY_REGION);
        credentialsProvider.refresh();
        // Create a connection to DynamoDB
        dbClient = new AmazonDynamoDBClient(credentialsProvider);

        // Create a table reference
        dbClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        dbTable = Table.loadTable(dbClient, DDB_TABLE);
    }
    public Document create(Document memo) {
        PutItemOperationConfig config = new PutItemOperationConfig();
        config.setReturnValue(ReturnValue.ALL_OLD);

//        if (!memo.containsKey("username")) {
//            memo.put("username", credentialsProvider.getCachedIdentityId());
//        }
//        if (!memo.containsKey("noteId")) {
//            memo.put("noteId", UUID.randomUUID().toString());
//        }
//        if (!memo.containsKey("creationDate")) {
//            memo.put("creationDate", System.currentTimeMillis());
//        }
        return dbTable.putItem(memo, config);
    }

    /**
     * Update an existing memo in the database
     * @param memo the memo to save
     */
    public void update(Document memo) {
        Document document = dbTable.updateItem(memo, new UpdateItemOperationConfig().withReturnValues(ReturnValue.ALL_NEW));
    }

    /**
     * Delete an existing memo in the database
     * @param memo the memo to delete
     */
    public void delete(Document memo) {
        dbTable.deleteItem(
                memo.get("userId").asPrimitive(),   // The Partition Key
                memo.get("noteId").asPrimitive());  // The Hash Key
    }

    /**
     * Retrieve a memo by noteId from the database
     * @param username the ID of the note
     * @return the related document
     */
    public Document getMemoById(String username) {
        Document doc = dbTable.getItem(new Primitive(username));
        return doc;
    }

    /**
     * Retrieve all the memos from the database
     * @return the list of memos
     */
    public List<Document> getAllMemos() {
        return dbTable.query(new Primitive(credentialsProvider.getCachedIdentityId())).getAllResults();
    }


}
