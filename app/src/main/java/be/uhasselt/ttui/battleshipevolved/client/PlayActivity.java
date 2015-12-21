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
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Arno on 9/12/2015.
 */
public class PlayActivity extends Activity {
    private Button mBtnSpeech;
    private TextView mTxtTurn;
    private TextView mTxtOnline;
    private TextView mTxtCooldown;
    private GridDrawer mGrid;
    private ArrayList<Cooldown> mCooldowns;
    private boolean itsmyturn;
    private PowerManager.WakeLock mWL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWL = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        mWL.acquire();
        itsmyturn = false;
        messageReceiver = new serverMessage();
        doBinding();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);
        mGrid = new GridDrawer(10, 10, (FrameLayout) findViewById(R.id.play_layout), this);
        mBtnSpeech = (Button) findViewById(R.id.speechButton);
        mBtnSpeech.setOnClickListener(mListenListener);
        mTxtTurn = (TextView) findViewById(R.id.turnText);
        mTxtOnline = (TextView) findViewById(R.id.onlineText);
        mTxtCooldown = (TextView) findViewById(R.id.cooldownText);
        mCooldowns = new ArrayList<Cooldown>();
        /*
         * NULL objects?
        mBoundService.sendMessage("Send cooldowns");
        mBoundService.sendMessage("Send turn");
        mBoundService.sendMessage("Send positions");
        */

        initSpeechListener();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbinding();
        mWL.release();
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }
    }
//
//    public void setOnline(ArrayList<String> online) {
//        String text = "";
//        for (int i  = 0; i < online.size(); i++){
//            text += online.get(i) + "\n";
//        }
//        mTxtOnline.setText(text);
//    }
//
//    public void setCooldowns(ArrayList<String> cooldowns) {
//        String text = "";
//        for (int i  = 0; i < cooldowns.size(); i++){
//            text += cooldowns.get(i) + "\n";
//        }
//        mTxtCooldown.setText(text);
//    }

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
     * Interprets a message given by the server and calls the appropriate function
     * @param message The message given by the server
     */
    private void interpretMessage(String message) {
        String[] words = message.split(" ");
        String command = words[0];
        if (message.equalsIgnoreCase("your turn")) {
            myTurn();
        } else if (message.equalsIgnoreCase("game start")) {
            clearCooldowns();
            mBoundService.sendMessage("Send cooldowns");
            mBoundService.sendMessage("Send turn");
            mBoundService.sendMessage("Send positions");
        } else if (command.equalsIgnoreCase("CoordinateUpdate")) {
            updateGrid(words);
        } else if (command.equalsIgnoreCase("next")) {
            updateTurn(words);
        } else if (command.equalsIgnoreCase("success")) {
            clearCooldowns();
            mBoundService.sendMessage("Send cooldowns");
        } else if (command.equalsIgnoreCase("cooldown")) {
            addCooldown(words);
        } else if (command.equalsIgnoreCase("position")) {
            addPosition(words);
        }


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
                    // TODO mGrid.setShip(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                    break;
            }
        } catch (IndexOutOfBoundsException e) {
            mBoundService.sendMessage("Could not interpret");
        }
    }

    private void updateTurn(String[] words) {
        try {
            int id = Integer.parseInt(words[4]);
            mTxtTurn.setText("Turn: Player " + id);
            if (itsmyturn) {
                itsmyturn = false;
                mBtnSpeech.setVisibility(View.INVISIBLE);
                clearCooldowns();
                mBoundService.sendMessage("Send cooldowns");
            }
        } catch (Exception e) {
            mBoundService.sendMessage("Could not interpret turn");
        }
    }

    private void myTurn(){
        mTxtTurn.setText("Your turn");
        itsmyturn = true;
        mBtnSpeech.setVisibility(View.VISIBLE);
    }

    private void addCooldown(String[] words) {
        mCooldowns.add(new Cooldown(words));
        updateCooldownTxt();
    }

    private void addPosition(String[] words) {
        try {
            // TODO mGrid.setShip(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
        } catch (Exception e) {

        }
    }

    private void clearCooldowns(){
        mCooldowns.clear();
    }

    private void updateCooldownTxt() {
        int bombOnline = 0;
        int bombCooldown = 0;
        int bombUsed = 0;
        String online = "";
        String offline = "";
        for (int i = 0; i < mCooldowns.size(); i++){
            Cooldown current = mCooldowns.get(i);
            if (current.getCooldown() == 0) {
                if (current.getWeapon().equals("Bomb")) {
                    bombOnline++;
                } else {
                    online += current.getWeapon() + "\n";
                }
            } else {
                if (current.getWeapon().equals("Bomb")) {
                    if (current.getCooldown() == 1)
                        bombCooldown++;
                    else
                        bombUsed++;
                } else {
                    offline += current.getWeapon() + " " + current.getCooldown() + " turn(s)\n";
                }
            }
        }
        if (bombOnline == 1) {
            online += "" + bombOnline + " Bomb\n";
        } else if (bombOnline > 1) {
            online += "" + bombOnline + " Bombs\n";
        }
        if (bombCooldown == 1) {
            offline += "" + bombCooldown + " Bomb 1 turn(s)\n"; //Bomb expected to have a cooldown of maximum 1 turn
        } else if (bombCooldown > 1) {
            offline += "" + bombCooldown + " Bombs 1 turn(s)\n";
        }
        if (bombUsed == 1) {
            offline += "" + bombUsed + " Bomb 2 turn(s)\n"; //Bomb expected to have a cooldown of maximum 2 turn
        } else if (bombCooldown > 1) {
            offline += "" + bombUsed + " Bombs 2 turn(s)\n";
        }

        mTxtOnline.setText(online);
        mTxtCooldown.setText(offline);
    }

    private class Cooldown{
        private String mWeapon;
        private int mCooldown;
        public Cooldown(String weapon, int cooldown){
            mWeapon = weapon;
            mCooldown = cooldown;
        }

        public Cooldown(String[] string){
            try {
                mWeapon = string[1];
                mCooldown = Integer.parseInt(string[2]);
            } catch (Exception e) {
                mBoundService.sendMessage("Could not interpret cooldown.");
            }
        }

        public String getWeapon() {return mWeapon;}
        public int getCooldown() {return mCooldown;}
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

    private View.OnClickListener mListenListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mIslistening)
                mSpeechRecognizer.stopListening();
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
    };


    private void initSpeechListener() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                //RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); //fuck free form
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
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
            System.out.println("Error occured while listening.");
            //mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            //Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {
            System.out.println("onPartialResults");
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
            if ((str.contains("end") || str.contains("and")) && (str.contains("turn") || str.contains("stern")) || str.contains("entering") || str.contains("anthem")) {
                //end turn!
                matchFound = true;
                endTurn();
            } else if (command.equalsIgnoreCase("shoot") || command.equalsIgnoreCase("shoots") || command.equalsIgnoreCase("shoes") || command.equalsIgnoreCase("should")) {
                words.remove(0);
                matchFound = handleShoot(words);
            } else if (command.equalsIgnoreCase("scan") || command.equalsIgnoreCase("skin")) {
                words.remove(0);
                matchFound = handleScan(words);
            } else if (command.equalsIgnoreCase("airstrike") || command.equalsIgnoreCase("inside")) {
                words.remove(0);
                matchFound = handleAirstrike(words);
            } else if (command.equalsIgnoreCase("air")) {
                words.remove(0);
                words.remove(0);
                matchFound = handleAirstrike(words);
            } else if (command.equalsIgnoreCase("artillery") || command.equalsIgnoreCase("activity") || command.equalsIgnoreCase("ability") || command.equalsIgnoreCase("hostility") || command.equalsIgnoreCase("octillery")) {
                words.remove(0);
                matchFound = handleArtillery(words);
            }
        }

        if (matchFound) {
            System.out.println("We found a match!");
        } else {
            System.out.println("Could not find a match, listening again");
            //mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }

    }

    private boolean wordEqualsPlayer(String str) {
        return (str.equalsIgnoreCase("player") || str.equalsIgnoreCase("play") || str.equalsIgnoreCase("playing") || str.equalsIgnoreCase("players") || str.equalsIgnoreCase("place") || str.equalsIgnoreCase("played") || str.equalsIgnoreCase("layer") || str.equalsIgnoreCase("there") || str.equalsIgnoreCase("their"));
    }

    private boolean handleShoot(ArrayList<String> words) {
        if (words.size() <= 0)
            return false;

        //next word must equal "player"
        if (!wordEqualsPlayer(words.get(0)))
            return false;

        words.remove(0); // remove the word "player"

        if (words.size() <= 0)
            return false;

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
        //next word must equal "player" or "play"
        if (words.size() <= 0)
            return false;

        if (!wordEqualsPlayer(words.get(0)))
            return false;

        words.remove(0); // remove the word "player"
        if (words.size() <= 0)
            return false;

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
        if (words.size() <= 0)
            return false;

        if (!wordEqualsPlayer(words.get(0)))
            return false;

        words.remove(0); // remove the word "player"
        if (words.size() <= 0)
            return false;

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
        if (player.equalsIgnoreCase("one") || player.equalsIgnoreCase("1") || player.equalsIgnoreCase("won")) {
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

        //some exception with 1 word
        String reply = try1WordExceptions(firstWord);
        if (!reply.isEmpty())
            return reply;

        //We'll need two words to interpret the coordinate
        if (words.size() < 2) {
            return "";
        }
        String secondWord = words.get(1);

        reply = tryWords(firstWord, secondWord);
        if (!reply.isEmpty())
            return reply;

        //other possibilities are wrong interpretations of some letters/numbers
        reply = try2WordExceptions(firstWord, secondWord);
        if (!reply.isEmpty())
            return reply;

        return "";
    }

    private String try1WordExceptions(String word) {
        if (word.equalsIgnoreCase("before"))
            return "B4";
        else if (word.equalsIgnoreCase("hi5"))
            return "H5";
        else if (word.equalsIgnoreCase("mp3"))
            return "B3";

        return "";
    }

    private String try2WordExceptions(String firstWord, String secondWord) {
        return "";
    }

    private String tryWords(String firstWord, String secondWord) {
        String out = "";
        //test the first word
        if (firstWord.equalsIgnoreCase("A") || firstWord.equalsIgnoreCase("8") || firstWord.equalsIgnoreCase("eight"))
            out = out.concat("A");
        else if (firstWord.equalsIgnoreCase("B") || firstWord.equalsIgnoreCase("be") || firstWord.equalsIgnoreCase("bee"))
            out = out.concat("B");
        else if (firstWord.equalsIgnoreCase("C") || firstWord.equalsIgnoreCase("see") || firstWord.equalsIgnoreCase("sea"))
            out = out.concat("C");
        else if (firstWord.equalsIgnoreCase("D") || firstWord.equalsIgnoreCase("the"))
            out = out.concat("D");
        else if (firstWord.equalsIgnoreCase("E"))
            out = out.concat("E");
        else if (firstWord.equalsIgnoreCase("F"))
            out = out.concat("F");
        else if (firstWord.equalsIgnoreCase("G") || firstWord.equalsIgnoreCase("gee"))
            out = out.concat("G");
        else if (firstWord.equalsIgnoreCase("H") || firstWord.equalsIgnoreCase("age") || firstWord.equalsIgnoreCase("page"))
            out = out.concat("H");
        else if (firstWord.equalsIgnoreCase("I") || firstWord.equalsIgnoreCase("eye"))
            out = out.concat("I");
        else if (firstWord.equalsIgnoreCase("J") || firstWord.equalsIgnoreCase("jay"))
            out = out.concat("J");
        else
            return "";

        //the second word
        if (secondWord.equalsIgnoreCase("1") || secondWord.equalsIgnoreCase("one") || secondWord.equalsIgnoreCase("won"))
            return out.concat("1");
        else if (secondWord.equalsIgnoreCase("2") || secondWord.equalsIgnoreCase("two") || secondWord.equalsIgnoreCase("to") || secondWord.equalsIgnoreCase("too"))
            return out.concat("2");
        else if (secondWord.equalsIgnoreCase("3") || secondWord.equalsIgnoreCase("three") || secondWord.equalsIgnoreCase("tree"))
            return out.concat("3");
        else if (secondWord.equalsIgnoreCase("4") || secondWord.equalsIgnoreCase("four") || secondWord.equalsIgnoreCase("for"))
            return out.concat("4");
        else if (secondWord.equalsIgnoreCase("5") || secondWord.equalsIgnoreCase("five"))
            return out.concat("5");
        else if (secondWord.equalsIgnoreCase("6") || secondWord.equalsIgnoreCase("six") || secondWord.equalsIgnoreCase("sex"))
            return out.concat("6");
        else if (secondWord.equalsIgnoreCase("7") || secondWord.equalsIgnoreCase("seven"))
            return out.concat("7");
        else if (secondWord.equalsIgnoreCase("8") || secondWord.equalsIgnoreCase("eight") || secondWord.equalsIgnoreCase("ate"))
            return out.concat("8");
        else if (secondWord.equalsIgnoreCase("9") || secondWord.equalsIgnoreCase("nine"))
            return out.concat("9");
        else if (secondWord.equalsIgnoreCase("10") || secondWord.equalsIgnoreCase("ten") || secondWord.equalsIgnoreCase("then") || secondWord.equalsIgnoreCase("than") || secondWord.equalsIgnoreCase("den"))
            return out.concat("10");

        return "";
    }

    /**
     * Dirty: set c manually to '0' if theres only 2 chars in the string
     */
    private boolean isCoordinate(char a, char b, char c) {
        if (a == 'A' || a == 'B' || a == 'C' || a == 'D' || a == 'E' || a == 'F' || a == 'G' || a == 'H' || a == 'I' || a == 'J' || a == 'a' || a == 'b' || a == 'c' || a == 'd' || a == 'e' || a == 'f' || a == 'g' || a == 'h' || a == 'i' || a == 'j') {
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
