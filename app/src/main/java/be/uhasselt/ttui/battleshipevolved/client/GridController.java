package be.uhasselt.ttui.battleshipevolved.client;

import android.app.Activity;
import android.view.Gravity;
import android.widget.AbsListView;
import android.widget.LinearLayout;
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
    private TextView mGrid[][];
    private Activity mParent;

    public GridController(int rows, int columns, TableLayout grid, Play play) {
        mRows = rows;
        mColumns = columns;
        mGridLayout = grid;
        mParent = play;
        mGrid = new TextView[mRows][mColumns];
        createGrid();
    }

    public void setShip(int row, int column) {
        try {
            mGrid[row][column].setBackgroundColor(mParent.getResources().getColor(R.color.colorShip));
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Out of grid bounds.");
        }
    }

    public void setDamaged(int row, int column) {
        try {
            mGrid[row][column].setBackgroundColor(mParent.getResources().getColor(R.color.colorDamage));
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Out of grid bounds.");
        }
    }

    public void setWater(int row, int column) {
        try {
            mGrid[row][column].setBackgroundColor(mParent.getResources().getColor(R.color.colorWater));
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Out of grid bounds.");
        }
    }

    private void createGrid() {
        TableRow numberRow = new TableRow(mParent);
        numberRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView placeholder = new TextView(mParent);
        placeholder.setWidth(50); //TODO: unfix size
        placeholder.setHeight(50);
        numberRow.addView(placeholder);
        for (int j = 0; j < mColumns; j++) {
            TextView number = new TextView(mParent);
            number.setGravity(Gravity.CENTER);
            number.setWidth(50); //TODO: unfix size
            number.setHeight(50);
            number.setText(j + "");
            number.setTextColor(mParent.getResources().getColor(R.color.colorCoordinate));
            numberRow.addView(number);
        }
        mGridLayout.addView(numberRow);

        for (int i = 0; i < mRows; i++) {
            TableRow tr = new TableRow(mParent);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView letter = new TextView(mParent);
            letter.setWidth(50); //TODO: unfix size
            letter.setHeight(50);
            letter.setGravity(Gravity.CENTER);
            letter.setText("" + (char) (i + 'A'));
            letter.setTextColor(mParent.getResources().getColor(R.color.colorCoordinate));
            tr.addView(letter);
            for (int j = 0; j < mColumns; j++) {
                TextView item = new TextView(mParent);
                item.setText(" ");
                item.setWidth(50); //TODO: unfix size
                item.setHeight(50);
                //item.setBackground(mParent.getResources().getDrawable(R.drawable.border));
                item.setBackgroundColor(mParent.getResources().getColor(R.color.colorFog));
                tr.addView(item);
                mGrid[i][j] = item;
            }
            mGridLayout.addView(tr);
        }
    }
}
