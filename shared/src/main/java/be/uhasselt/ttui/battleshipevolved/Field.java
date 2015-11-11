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

    /**
     * Places a ship on the field.
     * @param boat The ship to be placed.
     * @param anchor The top left point of the ship.
     * @param liesHorizontal The direction in which the ship is laying.
     * @pre The size of the ship >= 0
     */
    public void deployShip(Ship boat, Coordinate anchor, boolean liesHorizontal) throws InvalidShipPlacementException {
        int row = anchor.getRow();
        int column = anchor.getColumn();
        int boatSize = 4; //TODO: update line after boat.getSize() returns int;

        try {
            if (checkSpace(boatSize, anchor, liesHorizontal))
                if (liesHorizontal)
                    for (int i = 0; i < boatSize; i++)
                        mPositions[row][column + i] = boat;
                else
                    for (int i = 0; i < boatSize; i++)
                        mPositions[row + i][column] = boat;
            else
                throw new InvalidShipPlacementException("This ship crosses an earlier placed ship.");
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidShipPlacementException("This ship goes out of bounds.");
        }
    }

    private boolean checkSpace(int boatSize, Coordinate anchor, boolean liesHorizontal) throws IndexOutOfBoundsException {
        int row = anchor.getRow();
        int column = anchor.getColumn();
        boolean overwriting = false;

        if (liesHorizontal) {
            if (column + boatSize > COLUMNS)
                throw new IndexOutOfBoundsException();
            for (int i = 0; i < boatSize && !overwriting; i++){
                if (mPositions[row][column + i] != null)
                    overwriting = true;
            }
        }
        else {
            if (row + boatSize > ROWS)
                throw new IndexOutOfBoundsException();
            for (int i = 0; i < boatSize && !overwriting; i++){
                if (mPositions[row + i][column] != null)
                    overwriting = true;
            }
        }
        return !overwriting;
    }

    /**
     * Shoots and damages a ship on a certain coordinate
     * @param impact The coordinate where the missile impacts.
     * @return True if a ship has been damaged. False if it missed or if there already is shot here.
     */
    public boolean shoot(Coordinate impact) {
        int row = impact.getRow();
        int column = impact.getColumn();
        if (!mBeenShot[row][column]) {
            mBeenShot[row][column] = true;
            if (mPositions[row][column] != null) {
                mPositions[row][column].hit();
                return true; //Ship was shot
            }
            return false; //Shot missed
        }
        return false; //Already shot here
    }

    public boolean isShot(Coordinate coor) {
        return mBeenShot[coor.getRow()][coor.getColumn()];
    }

    public boolean boatPlacedAt(Coordinate coor) {
        return (mPositions[coor.getRow()][coor.getColumn()] != null);
    }
}
