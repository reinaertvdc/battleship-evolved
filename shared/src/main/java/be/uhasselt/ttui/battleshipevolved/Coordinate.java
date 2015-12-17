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
        char row, column;
        row = string.charAt(0);
        column = string.charAt(1);
        if (row >= 'A' && row <= 'J')
            mRow = row - 'A';
        else return false;
        if (column >= '0' && column <= '9')
            mColumn = column - '0' - 1; // column titled '1' has index 0
        else return false;


        return true;
    }
}
