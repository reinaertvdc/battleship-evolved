package be.uhasselt.ttui.battleshipevolved.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import be.uhasselt.ttui.battleshipevolved.Coordinate;

/**
 * Created by Arno on 9/12/2015.
 */
public class Play  extends Activity {
    private TextView mTxtOnline;
    private TextView mTxtCooldown;
    private GridController mGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        messageReceiver = new serverMessage();
        doBinding();
        setContentView(R.layout.activity_play);
        mGrid = new GridController(10, 10, (TableLayout) findViewById(R.id.gridLayout), this);
        mTxtOnline = (TextView) findViewById(R.id.onlineText);
        mTxtCooldown = (TextView) findViewById(R.id.cooldownText);

        initSpeechListener();

        //Test values:
        ArrayList<String> testOnline = new ArrayList<>();
        testOnline.add("Airstrike online");
        testOnline.add("Radar online");
        setOnline(testOnline);
        ArrayList<String> testCooldown = new ArrayList<>();
        testCooldown.add("Missile 1 turn");
        testCooldown.add("Bomb 2 turns");
        setCooldowns(testCooldown);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbinding();
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }
    }

    public void setOnline(ArrayList<String> online) {
        String text = "";
        for (int i  = 0; i < online.size(); i++){
            text += online.get(i) + "\n";
        }
        mTxtOnline.setText(text);
    }

    public void setCooldowns(ArrayList<String> cooldowns) {
        String text = "";
        for (int i  = 0; i < cooldowns.size(); i++){
            text += cooldowns.get(i) + "\n";
        }
        mTxtCooldown.setText(text);
    }

    private serverMessage messageReceiver;
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(messageReceiver, new IntentFilter("SERVER_MESSAGE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageReceiver);
    }

    public class serverMessage extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(action.equalsIgnoreCase("SERVER_MESSAGE")){
                Bundle extra = intent.getExtras();
                String message = extra.getString("message");
                System.out.println(message);
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();
                if (!mIslistening)
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
        }
    }

    /**
     * Interprets a message given by a client and calls the appropriate function
     * @param message The message given by the client
     */
    private void interpretMessage(String message) {
        String[] words = message.split(" ");
        String command = words[0];
        if (command.equalsIgnoreCase("CoordinateUpdate")) {
            updateGrid(words);
        } /*else if (command.equalsIgnoreCase("shoot")) {
            handleShoot(words);
        } else if (command.equalsIgnoreCase("scan")) {
            handleScan(words);
        } else if (command.equalsIgnoreCase("airstrike")) {
            handleAirstrike(words);
        } else if (message.equalsIgnoreCase("end turn")) {
            handleEndTurn();
        }

        else {
            sendMessage("Could not interpret " + command);
        }*/
    }
    //"CoordinateUpdate " + cs.getRow() + " " + cs.getColumn() + " " + cs.getStatus();
    private void updateGrid(String[] words){
        try {
            if (words[3] == "HIT") {
                mGrid.setDamaged(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                // Vibrate for 500 milliseconds
                Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
                //sendMessage("Succes");
            }
        } catch (IndexOutOfBoundsException e) {
            //sendMessage("Could not interpret");
        }
    }

    //functions for binding to the connectionThread service
    private boolean isBound = false;
    private ConnectionThread mBoundService = null;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((ConnectionThread.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }
    };
    private void doBinding() {
        bindService(new Intent(this, ConnectionThread.class), mConn, Context.BIND_AUTO_CREATE);
        isBound = true;
    }
    private void doUnbinding() {
        if (isBound) {
            unbindService(mConn);
            isBound = false;
        }
    }
    //end functions for binding

    //functions for speech input
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening;

    private void initSpeechListener() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());


        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            System.out.println("onBeginningOfSpeech");
            //Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            System.out.println("onEndOfSpeech");
            //Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            System.out.println("Error occured while listening. Resumed Listening");
            //Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            //Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do
            System.out.println(matches.size() + " matches found:");
            for (int i = 0; i < matches.size(); i++) {
                System.out.println(matches.get(i));
            }
        }
        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }
}
