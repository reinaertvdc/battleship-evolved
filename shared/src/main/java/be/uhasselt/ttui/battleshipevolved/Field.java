package be.uhasselt.ttui.battleshipevolved;

import java.util.Arrays;

/**
 * Field describes where every ship is located.
 * @author Arno Stienaers
 */
public class Field {
    public static int ROWS = 10;
    public static int COLUMNS = 10;
    public enum CoorStatus {FOG, MISSED, HIT};

    private Ship mPositions[][];
    private boolean mBeenShot[][];
    private CoorStatus[][] mVisibleField;
    private boolean mFieldRevealed;

    public Field() {
        mPositions = new Ship[ROWS][COLUMNS];
        mBeenShot = new boolean[ROWS][COLUMNS];
        mVisibleField = new CoorStatus[ROWS][COLUMNS];
        Arrays.fill(mVisibleField, CoorStatus.FOG);
    }

    /**
     * Places a ship on the field.
     * @param boat The ship to be placed.
     * @param anchor The top left point of the ship.
     * @param liesHorizontal The direction in which the ship is laying.
     * @pre The size of the ship >= 0
     * @pre Shots have not been fired yet.
     * @post Visibility is not changed.
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
        CoorStatus status;
        if (boatPlacedAt(coor))
            status = CoorStatus.HIT;
        else
            status = CoorStatus.MISSED;
        mVisibleField[coor.getRow()][coor.getColumn()] = status;
        mFieldRevealed = true;
    }

    /**
     * Refreshes the entire field
     * @post mFieldRevealed = false
     */
    public void refreshField() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                mVisibleField[i][j] = getStatus(new Coordinate(i, j));
            }
        }
        mFieldRevealed = false;
    }

    private boolean isShot(Coordinate coor) {
        return mBeenShot[coor.getRow()][coor.getColumn()];
    }

    private boolean boatPlacedAt(Coordinate coor) {
        return (mPositions[coor.getRow()][coor.getColumn()] != null);
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

    private CoorStatus getStatus(Coordinate coor) {
        CoorStatus status;
        if (isShot(coor))
            if (boatPlacedAt(coor))
                status = CoorStatus.HIT;
            else
                status = CoorStatus.MISSED;
        else
            status = CoorStatus.FOG;
        return status;
    }

    /**
     * Update a single coordinate on a field
     * @param coor The coordinate that needs to be updated
     */
    private void updateCoor(Coordinate coor){
        mVisibleField[coor.getRow()][coor.getColumn()] = getStatus(coor);
    }
}
