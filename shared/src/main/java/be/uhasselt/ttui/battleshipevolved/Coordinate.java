package be.uhasselt.ttui.battleshipevolved;

public class Coordinate {
    private int mRow;
    private int mColumn;

    public Coordinate(int row, int column) {
        mRow = row;
        mColumn = column;
        //TODO: IndexOutOfBoundsException
    }

    public Coordinate(char row, int column) {
        if (row >= '0' && row <= '9')
            mRow = row - '0';
        mColumn = column;
    }

    public int getRow() {
        return mRow;
    }

    public int getColumn() {
        return mColumn;
    }
}
