package be.uhasselt.ttui.battleshipevolved.client;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Arno on 9/12/2015.
 */
public class Play  extends Activity {
    private TextView mTxtCooldown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.play);
        mTxtCooldown = (TextView) findViewById(R.id.cooldownText);
        mTxtCooldown.setText("Loaded");

    }
}
