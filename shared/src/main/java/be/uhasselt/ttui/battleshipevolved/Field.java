package be.uhasselt.ttui.battleshipevolved;

/**
 * Field describes where every ship is located.
 *
 * @author Arno Stienaers
 */

public class Field {
    public static int ROWS = 10;
    public static int COLUMNS = 10;

    private Ship mPositions[][];
    private boolean mBeenShot[][];

    public Field() {
        mPositions = new Ship[ROWS][COLUMNS];
        mBeenShot = new boolean[ROWS][COLUMNS];
    }

    public void deployShip(Ship boat, Coordinate anchor, boolean liesHorizontal) {
        int row = anchor.getRow();
        int column = anchor.getColumn();
        boolean overwriting = false;
        //TODO: Fix java equivalent for Ship pointer

        if (liesHorizontal) {
            for (int i = 0; i < boat.getSize() && !overwriting; i++) {
                if (mPositions[row][column + i] == null)
                    mPositions[row][column + i] = boat;
                else
                    overwriting = true;
            }
        }
        else {
            for (int i = 0; i < boat.getSize() && !overwriting; i++) {
                if (mPositions[row + i][column] == null)
                    mPositions[row + i][column] = boat;
                else
                    overwriting = true;
            }
        }
        //TODO: overwritten protocol
    }

    public boolean shoot(Coordinate impact) {
        int row = impact.getRow();
        int column = impact.getColumn();
        if (!mBeenShot[row][column]) {
            mBeenShot[row][column] = true;
            if (mPositions[row][column] != null) {
                mPositions[row][column].hit();
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean isShot(Coordinate coor) {
        return mBeenShot[coor.getRow()][coor.getColumn()];
    }

    public boolean boatPlacedAt(Coordinate coor) {
        return (mPositions[coor.getRow()][coor.getColumn()] != null);
    }
}
