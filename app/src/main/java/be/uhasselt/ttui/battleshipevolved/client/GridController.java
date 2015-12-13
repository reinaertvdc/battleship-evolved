package be.uhasselt.ttui.battleshipevolved.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by Arno on 8/12/2015.
 */
public class GridController {
    private int mRows;
    private int mColumns;
    private TableLayout mGridLayout;
    private ImageView mGrid[][];
    private Activity mParent;

    public GridController(int rows, int columns, TableLayout grid, Play play) {
        mRows = rows;
        mColumns = columns;
        mGridLayout = grid;
        mParent = play;
        mGrid = new ImageView[mRows][mColumns];
        //createGrid(); //TODO: Stop createGrid from crashing.
    }

    private void createGrid() {
        TableRow numberRow = new TableRow(mParent);
        numberRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        numberRow.addView(null);
        for (int j = 0; j < mColumns; j++) {
            TextView number = new TextView(mParent);
            number.setText(j);
            numberRow.addView(number);
        }
        mGridLayout.addView(numberRow);

        for (int i = 0; i < mRows; i++) {
            TableRow tr = new TableRow(mParent);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView letter = new TextView(mParent);
            letter.setText((char) (i + 'A'));
            tr.addView(letter);
            for (int j = 0; j < mColumns; j++) {
                ImageView image = new ImageView(mParent);
                image.setBackgroundColor(mParent.getResources().getColor(R.color.colorWater));
                tr.addView(image);
                mGrid[i][j] = image;
            }
            mGridLayout.addView(tr);
        }
    }
}
