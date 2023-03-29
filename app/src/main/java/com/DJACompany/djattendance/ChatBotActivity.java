package com.DJACompany.djattendance;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lex.interactionkit.InteractionClient;
import com.amazonaws.mobileconnectors.lex.interactionkit.config.InteractionConfig;
import com.amazonaws.mobileconnectors.lex.interactionkit.Response;
import com.amazonaws.mobileconnectors.lex.interactionkit.continuations.LexServiceContinuation;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.AudioPlaybackListener;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.InteractionListener;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexrts.model.DialogState;


import java.text.DateFormat;
import java.util.Date;

public class ChatBotActivity extends Activity {
    private static final String TAG = "ChatBotActivity";
    /**
     * Implementing {@link AudioPlaybackListener}.
     */
    private EditText userTextInput;
    private InteractionClient lexInteractionClient;
    private boolean inConversation;
    private LexServiceContinuation convContinuation;
    final InteractionListener interactionListener = new InteractionListener() {
        @Override
        public void onReadyForFulfillment(final Response response) {
            Log.d(TAG, "Transaction completed successfully");
            addMessage(new TextMessage(response.getTextResponse(), "rx", getCurrentTimeStamp()));
            inConversation = true;
        }

        @Override
        public void promptUserToRespond(final Response response,
                                        final LexServiceContinuation continuation) {
            addMessage(new TextMessage(response.getTextResponse(), "rx", getCurrentTimeStamp()));
            readUserText(continuation);
        }

        @Override
        public void onInteractionError(final Response response, final Exception e) {
            Log.e(TAG, "Interaction error", e);
            if (response != null) {
                if (DialogState.Failed.toString().equals(response.getDialogState())) {
                    addMessage(new TextMessage(response.getTextResponse(), "rx",
                            getCurrentTimeStamp()));
                    inConversation = false;
                } else {
                    addMessage(new TextMessage("Please retry", "rx", getCurrentTimeStamp()));
                }
            } else {
                showToast("Error: " + e.getMessage());
                Log.e(TAG, "Interaction error", e);
                inConversation = false;
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        init();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Initializes the application.
     */
    private void init() {
        Log.d(TAG, "Initializing text component: ");
        userTextInput = (EditText) findViewById(R.id.userInputEditText);

        // Set text edit listener.
        userTextInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    textEntered();
                    return true;
                }
                return false;
            }
        });
        userTextInput.setEnabled(false);

        initializeLexSDK();
        startNewConversation();
    }

    /**
     * Initializes Lex client.
     */
    private void initializeLexSDK() {
        InitializeTask task = new InitializeTask();
        task.execute("");
    }

    /**
     * Read user text input.
     */
    private void textEntered() {
        String text = userTextInput.getText().toString();

        if (text == null || text.trim().equals("")) {
            Log.d(TAG, "text null or empty");
            return;
        }

        if (!inConversation) {
            Log.d(TAG, " -- New conversation started");
            startNewConversation();
            addMessage(new TextMessage(text, "tx", getCurrentTimeStamp()));
            Log.d("TExt", text);
            lexInteractionClient.textInForTextOut(text, null);
            inConversation = true;
        } else {
            Log.d(TAG, " -- Responding with text: " + text);
            if(convContinuation == null){
                startNewConversation();
                addMessage(new TextMessage(text, "tx", getCurrentTimeStamp()));
                lexInteractionClient.textInForTextOut(text, null);
                inConversation = true;
            }
            else{
                addMessage(new TextMessage(text, "tx", getCurrentTimeStamp()));
                convContinuation.continueWithTextInForTextOut(text);
            }
        }
        clearTextInput();
    }

    /**
     * Pass user input to Lex client.
     *
     * @param continuation
     */
    private void readUserText(final LexServiceContinuation continuation) {
        convContinuation = continuation;
        inConversation = true;
    }

    /**
     * Clears the current conversation history and closes the current request.
     */
    private void startNewConversation() {
        Log.d(TAG, "Starting new conversation");
        Conversation.clear();
        inConversation = false;
        clearTextInput();
    }

    /**
     * Clear text input field.
     */
    private void clearTextInput() {
        userTextInput.setText("");
    }

    /**
     * Show the text message on the screen.
     *
     * @param message
     */
    private void addMessage(final TextMessage message) {
        Conversation.add(message);
        final MessagesListAdapter listAdapter = new MessagesListAdapter(getApplicationContext());
        final ListView messagesListView = (ListView) findViewById(R.id.conversationListView);
        messagesListView.setDivider(null);
        messagesListView.setAdapter(listAdapter);
        messagesListView.setSelection(listAdapter.getCount() - 1);
    }

    /**
     * Current time stamp.
     *
     * @return
     */
    private String getCurrentTimeStamp() {
        return DateFormat.getDateTimeInstance().format(new Date());
    }

    /**
     * Show a toast.
     *
     * @param message - Message text for the toast.
     */
    private void showToast(final String message) {
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        Log.d(TAG, message);
    }

    private class InitializeTask extends AsyncTask<String, Integer, Long> {

        protected Long doInBackground(String... params) {
            Log.d(TAG, "Lex Client");
            Log.d(TAG, "identityId: ");

            String botName = "EnquireAboutHostel";
            String botAlias = "Vesion_One";

            String COGNITO_IDENTITY_POOL_ID = "us-west-2:98dadbe5-9129-4bea-a032-a99334ba5516";
            Regions COGNITO_IDENTITY_REGION = Regions.US_WEST_2;

            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(), COGNITO_IDENTITY_POOL_ID, COGNITO_IDENTITY_REGION);
            credentialsProvider.refresh();
            InteractionConfig lexInteractionConfig = new InteractionConfig(
                    botName,
                    botAlias,
                    credentialsProvider.getCachedIdentityId());
            lexInteractionClient = new InteractionClient(getApplicationContext(),
                    credentialsProvider,
                    Regions.US_WEST_2,
                    lexInteractionConfig);

            lexInteractionClient.setInteractionListener(interactionListener);

            Log.d(TAG, "Initialized: ");
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            // setProgressPercent(progress[0]);
        }

        // the onPostexecute method receives the return type of doInBackGround()
        protected void onPostExecute(Long result) {
            // do something with the result, for example display the received Data in a ListView
            // in this case, "result" would contain the "someLong" variable returned by doInBackground();
            runOnUiThread(() -> userTextInput.setEnabled(true));
        }
    }
}