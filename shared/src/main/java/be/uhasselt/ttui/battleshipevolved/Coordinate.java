package be.uhasselt.ttui.battleshipevolved;

public class Coordinate {
    private int mRow;
    private int mColumn;

    public Coordinate(int row, int column) {
        mRow = row;
        mColumn = column;
    }

    public int getRow() {
        return mRow;
    }

    public int getColumn() {
        return mColumn;
    }
}
