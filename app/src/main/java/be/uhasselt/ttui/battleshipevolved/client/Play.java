package be.uhasselt.ttui.battleshipevolved.client;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

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

        setContentView(R.layout.activity_play);
        mGrid = new GridController(10, 10, (TableLayout) findViewById(R.id.gridLayout), this);
        mTxtOnline = (TextView) findViewById(R.id.onlineText);
        mTxtCooldown = (TextView) findViewById(R.id.cooldownText);

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
}
