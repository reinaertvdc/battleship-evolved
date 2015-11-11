package be.uhasselt.ttui.battleshipevolved;

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
}
