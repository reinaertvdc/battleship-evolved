package be.uhasselt.ttui.battleshipevolved.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Arno on 9/12/2015.
 */
public class Play  extends Activity {
    private TextView mTxtTurn;
    private TextView mTxtOnline;
    private TextView mTxtCooldown;
    private GridController mGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        messageReceiver = new serverMessage();
        doBinding();
        setContentView(R.layout.activity_play);
        mGrid = new GridController(10, 10, (TableLayout) findViewById(R.id.gridLayout), this);
        mTxtTurn = (TextView) findViewById(R.id.turnText);
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
                interpretMessage(message);
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
        if (message.equalsIgnoreCase("your turn")) {
            if (!mIslistening)
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        } else if (command.equalsIgnoreCase("CoordinateUpdate")) {
            updateGrid(words);
        } else if (command.equalsIgnoreCase("next")) {
            updateTurn(words);
        } /*else if (command.equalsIgnoreCase("scan")) {
            handleScan(words);
        } else if (command.equalsIgnoreCase("airstrike")) {
            handleAirstrike(words);
        } else if (message.equalsIgnoreCase("end turn")) {
            handleEndTurn();
        }*/

        else {
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void updateGrid(String[] words){
        try {
            switch (words[3]) {
                case "FOG":
                    break;
                case "HIT":
                    mGrid.setDamaged(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                    // Vibrate for 500 milliseconds
                    Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                    break;
                case "MISSED":
                    mGrid.setWater(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                    break;
                case "BOAT":
                    mGrid.setShip(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                    break;
            }
        } catch (IndexOutOfBoundsException e) {
            mBoundService.sendMessage("Could not interpret");
        }
    }

    private void updateTurn(String[] words) {
        try {
            mTxtTurn.setText("Turn: Player " + Integer.parseInt(words[4]));
        } catch (Exception e) {
            mBoundService.sendMessage("Could not interpret");
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
            interpretVoiceCommand(matches);
        }
        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }

    private void interpretVoiceCommand(ArrayList<String> matches) {
        boolean matchFound = false;
        for (int i = 0; i < matches.size() && !matchFound; i++) {
            String str = matches.get(i);
            ArrayList<String> words = new ArrayList<>(Arrays.asList(str.split(" ")));
            String command = words.get(0);
            if (str.contains("end") && str.contains("turn")) {
                //end turn!
                matchFound = true;
                endTurn();
            } else if (command.equalsIgnoreCase("shoot")) {
                words.remove(0);
                matchFound = handleShoot(words);
            } else if (command.equalsIgnoreCase("scan")) {
                words.remove(0);
                matchFound = handleScan(words);
            } else if (command.equalsIgnoreCase("airstrike")) {
                words.remove(0);
                matchFound = handleAirstrike(words);
            } else if (command.equalsIgnoreCase("artillery")) {
                words.remove(0);
                matchFound = handleArtillery(words);
            }
        }

        if (matchFound) {

        } else {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }

    }

    private boolean handleShoot(ArrayList<String> words) {
        //next word must equal "player"
        if (!words.get(0).equalsIgnoreCase("player")) {
            return false;
        }

        words.remove(0); // remove the word "player"
        //next word is the player we want to shoot
        int playerID = interpretPlayer(words.get(0));
        if (playerID <= 0) {
            return false; //return false when the player is 0 or lower
        }
        words.remove(0); //remove the player word
        //next word(s) are the coordinates
        String coord = interpretCoordinates(words);

        if (coord.isEmpty()) {
            return false;
        }

        shoot(playerID, coord);
        return true;
    }

    private boolean handleScan(ArrayList<String> words) {
        //next word must equal "player"
        if (!words.get(0).equalsIgnoreCase("player")) {
            return false;
        }

        words.remove(0); // remove the word "player"
        //next word is the player we want to scan
        int playerID = interpretPlayer(words.get(0));
        if (playerID <= 0) {
            return false; //return false when the player is 0 or lower
        }
        words.remove(0); //remove the player word
        //next word(s) are the coordinates
        String coord = interpretCoordinates(words);

        if (coord.isEmpty()) {
            return false;
        }

        scan(playerID, coord);
        return true;
    }

    private boolean handleAirstrike(ArrayList<String> words) {
        //next word must equal "player"
        if (!words.get(0).equalsIgnoreCase("player")) {
            return false;
        }

        words.remove(0); // remove the word "player"
        //next word is the player we want to airstrike
        int playerID = interpretPlayer(words.get(0));
        if (playerID <= 0) {
            return false; //return false when the player is 0 or lower
        }
        words.remove(0); //remove the player word
        //next word(s) are the coordinates
        String coord = interpretCoordinates(words);

        if (coord.isEmpty()) {
            return false;
        }

        airstrike(playerID, coord);
        return true;
    }

    private boolean handleArtillery(ArrayList<String> words) {
        //next word(s) are the coordinates
        String coord = interpretCoordinates(words);

        if (coord.isEmpty()) {
            return false;
        }

        artillery(coord);
        return true;
    }

    private int interpretPlayer(String player) {
        if (player.equalsIgnoreCase("one") || player.equalsIgnoreCase("1")) {
            return 1;
        } else if (player.equalsIgnoreCase("two") || player.equalsIgnoreCase("to") || player.equalsIgnoreCase("2")) {
            return 2;
        } else if (player.equalsIgnoreCase("three") || player.equalsIgnoreCase("tree") || player.equalsIgnoreCase("3")) {
            return 3;
        } else if (player.equalsIgnoreCase("four") || player.equalsIgnoreCase("for") || player.equalsIgnoreCase("4")) {
            return 4;
        }
        //no matching player found
        return -1;
    }

    private String interpretCoordinates(ArrayList<String> words) {
        //first check if the coordinate was interpreted correctly
        if (words.size() == 0)
            return "";
        String firstWord = words.get(0);
        //if the word is "on", we'll just skip it
        if (firstWord.equalsIgnoreCase("on")) {
            words.remove(0);
            firstWord = words.get(0);
        }
        if (firstWord.length() == 2) {
            if (isCoordinate(firstWord.charAt(0), firstWord.charAt(1), '0'))
                return firstWord;
        } else if (firstWord.length() == 3) {
            if (isCoordinate(firstWord.charAt(0), firstWord.charAt(1), firstWord.charAt(2)))
                return firstWord;
        }

        //other possibilities are wrong interpretations of some letters/numbers
        if (words.size() < 2)
            return "";
        String secondWord = words.get(1);
        //a tree
        if (firstWord.equalsIgnoreCase("a") && secondWord.equalsIgnoreCase("tree")) {
            return "A3";
        }


        return "";
    }

    /**
     * Dirty: set c manually to '0' if theres only 2 chars in the string
     */
    private boolean isCoordinate(char a, char b, char c) {
        if (a == 'A' || a == 'B' || a == 'C' || a == 'D' || a == 'E' || a == 'F' || a == 'G' || a == 'H' || a == 'I' || a == 'J') {
            if (b == '1' || b == '2' || b == '3' || b == '4' || b == '5' || b == '6' || b == '7' || b == '8' || b == '9') {
                if (c == '0') {
                    return true;
                }
            }
        }
        return false;
    }


    /*
     * Functions to do shit and send the appropriate messages to the server
     */
    private void endTurn() {
        mBoundService.sendMessage("end turn");
    }

    private void shoot(int player, String coord) {
        mBoundService.sendMessage("shoot " + player + " " + coord);
    }

    private void scan(int player, String coord) {
        mBoundService.sendMessage("scan " + player + " " + coord);
    }

    private void airstrike(int player, String coord) {
        mBoundService.sendMessage("airstrike " + player + " " + coord);
    }

    private void artillery(String coord) {
        mBoundService.sendMessage("artillery " + coord);
    }
}
