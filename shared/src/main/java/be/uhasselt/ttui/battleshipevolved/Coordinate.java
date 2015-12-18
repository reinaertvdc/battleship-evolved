package be.uhasselt.ttui.battleshipevolved;

/**
 * Simple datapassing coordinate class
 * @Author Arno Stienaers
 */
public class Coordinate {
    private int mRow;
    private int mColumn;

    public Coordinate(int row, int column) {
        if (row < 0 || row > Field.ROWS || column < 0 || column > Field.COLUMNS )
            throw new IndexOutOfBoundsException();
        mRow = row;
        mColumn = column;
    }

    public Coordinate(char row, int column) {
        if (row >= '0' && row <= '9')
            mRow = row - '0';
        else
            throw new IndexOutOfBoundsException();
        if (column >= 0 && column <= Field.COLUMNS )
            mColumn = column;
        else
            throw new IndexOutOfBoundsException();
    }

    public int getRow() {
        return mRow;
    }

    public int getColumn() {
        return mColumn;
    }

    public boolean setFromString(String string) {
        //normal case
        char row, column, column2;
        row = string.charAt(0);
        column = string.charAt(1);

        if (row >= 'A' && row <= 'J') {
            mRow = row - 'A';
        }
        else return false;
        if (column >= '0' && column <= '9') {
            mColumn = column - '0' - 1; // column titled '1' has index 0
            if (string.length() > 2) {
                //if there is another character, we'll have to process this one as well
                column2 = string.charAt(2);
                if (column2 >= '0' && column <= '9') {
                    mColumn = (mColumn+1) * 10; //we'll have to undo the previous -1 if we want this algorithm to work
                    //trust me, i'm an engineer lol
                    mColumn += column2 - '0' - 1;
                } else return false;
            }
        }
        else return false;


        return true;
    }
}
