package be.uhasselt.ttui.battleshipevolved;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;

/**
 * Field describes where every ship is located.
 * @author Arno Stienaers
 */
public class Field extends Observable {
    public static int ROWS = 10;
    public static int COLUMNS = 10;

    private Ship mPositions[][];
    private boolean mBeenShot[][];
    private CoordinateStatus.Status[][] mVisibleField;
    private boolean mFieldRevealed;

    public Field() {
        mPositions = new Ship[ROWS][COLUMNS];
        mBeenShot = new boolean[ROWS][COLUMNS];
        mVisibleField = new CoordinateStatus.Status[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            Arrays.fill(mVisibleField[i], CoordinateStatus.Status.FOG);
        }
        int i = 5;
        i++;
    }

    /**
     * Places a ship on the field.
     * @param boat The ship to be placed.
     * @param anchor The top left point of the ship.
     * @param liesHorizontal The direction of the ship.
     * @pre Shipsize >= 0 (both row and column)
     * @pre Shots have not been fired yet.
     * @post Visibility is not changed.
     */
    public void deployShip(Ship boat, Coordinate anchor, boolean liesHorizontal) throws InvalidShipPlacementException {
        int row = anchor.getRow();
        int column = anchor.getColumn();
        int endRow = boat.getSize().getRow() + row;
        int endColumn = boat.getSize().getColumn() + column;

        if (!liesHorizontal) {
            int temp = endRow;
            endRow = endColumn;
            endColumn = temp;
        }

        try {
            if (checkSpace(anchor, new Coordinate(endRow, endColumn))) //New Coordinate because of the possible direction switch
                for (int i = row; i < endRow; i++) {
                    for (int j = column; j < endColumn; j++) {
                        mPositions[i][j] = boat;
                    }
                }
            else
                throw new InvalidShipPlacementException("This ship crosses an earlier placed ship.");
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidShipPlacementException("This ship goes out of bounds.");
        }
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
                updateCoor(impact);
                return true; //Ship was shot
            }
            updateCoor(impact);
            return false; //Shot missed
        }
        return false; //Already shot here
    }

    /**
     * Reveals a single coordinate on a field
     * @param coor The coordinate that needs to be revealed
     * @post mFieldsReviewed[i] = true
     */
    public void reveal(Coordinate coor){
        if (!mBeenShot[coor.getRow()][coor.getColumn()]) {
            CoordinateStatus.Status status;
            if (boatPlacedAt(coor))
                status = CoordinateStatus.Status.BOAT;
            else
                status = CoordinateStatus.Status.MISSED;
            mVisibleField[coor.getRow()][coor.getColumn()] = status;
            mFieldRevealed = true;
        }
    }

    /**
     * Refreshes the entire field
     * @post mFieldRevealed = false
     */
    public void refreshField() {
        ArrayList<CoordinateStatus> fieldStatus = new ArrayList<CoordinateStatus>();
        CoordinateStatus.Status currentStatus;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                currentStatus = getStatus(new Coordinate(i, j));
                mVisibleField[i][j] = currentStatus;
                fieldStatus.add(new CoordinateStatus(i, j, currentStatus));
            }
        }
        mFieldRevealed = false;
        notifyObservers(fieldStatus);
    }

    private boolean isShot(Coordinate coor) {
        return mBeenShot[coor.getRow()][coor.getColumn()];
    }

    private boolean boatPlacedAt(Coordinate coor) {
        return (mPositions[coor.getRow()][coor.getColumn()] != null);
    }

    private boolean checkSpace(Coordinate anchor, Coordinate otherAnchor) throws IndexOutOfBoundsException {
        int row = anchor.getRow();
        int column = anchor.getColumn();
        int endRow = otherAnchor.getRow() + row;
        int endColumn = otherAnchor.getColumn() + column;
        boolean overwriting = false;

        if (endRow > ROWS || endColumn > COLUMNS)
            throw new IndexOutOfBoundsException();

        for (int i = row; i < endRow && !overwriting; i++) {
            for (int j = column; j < endColumn && !overwriting; j++) {
                if (mPositions[i][j] != null)
                    overwriting = true;
            }
        }

        return !overwriting;
    }

    private CoordinateStatus.Status getStatus(Coordinate coor) {
        CoordinateStatus.Status status;
        if (isShot(coor))
            if (boatPlacedAt(coor))
                status = CoordinateStatus.Status.HIT;
            else
                status = CoordinateStatus.Status.MISSED;
        else
            status = CoordinateStatus.Status.FOG;
        return status;
    }

    private void updateCoor(Coordinate coor){
        mVisibleField[coor.getRow()][coor.getColumn()] = getStatus(coor);
        notifyObservers(new CoordinateStatus(coor, getStatus(coor)));
    }
}
