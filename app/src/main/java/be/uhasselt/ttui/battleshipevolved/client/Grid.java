package be.uhasselt.ttui.battleshipevolved.client;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Arno on 8/12/2015.
 */
public class Grid extends Activity{
    public Grid (int rows, int columns) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.grid);

    }
}
