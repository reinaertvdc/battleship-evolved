package be.uhasselt.ttui.battleshipevolved;

import java.util.ArrayList;

/**
 * Board holds every players field and makes it easy for the UI
 * @author Arno Stienaers
 */
public class Board {
    public enum CoorStatus {FOG, MISSED, HIT};
    private ArrayList<Field> mFields;
    private ArrayList<CoorStatus[][]> mVisibleFields;
    private boolean[] mFieldsReviewed;

    public Board(ArrayList<Field> fields) {
        mFields = fields;
        mVisibleFields = new ArrayList<CoorStatus[][]>();
        mFieldsReviewed = new boolean[mFields.size()];
        for (int i = 0; i < mFields.size(); i++){
            CoorStatus[][] visibleField = new CoorStatus[Field.ROWS][Field.COLUMNS];
            mVisibleFields.add(i,visibleField);
            updateField(mFields.get(i));
        }
    }

    /**
     * Update a single coordinate on a field
     * @param field The field on which the coordinate is placed
     * @param coor The coordinate that needs to be updated
     */
    public void updateCoor(Field field, Coordinate coor){
        int index = mFields.indexOf(field);
        if (index >= 0) {
            mVisibleFields.get(index)[coor.getRow()][coor.getColumn()] = getStatus(field, coor);
        }
        //TODO: Else error exception
    }

    /**
     * Reveals a single coordinate on a field
     * @param field The field on which the coordinate is placed
     * @param coor The coordinate that needs to be revealed
     * @post mFieldsReviewed[i] = true
     */
    public void revealCoor(Field field, Coordinate coor){
        int index = mFields.indexOf(field);
        if (index >= 0) {
            CoorStatus status;
            if (field.boatPlacedAt(coor))
                status = CoorStatus.HIT;
            else
                status = CoorStatus.MISSED;
            mVisibleFields.get(index)[coor.getRow()][coor.getColumn()] = status;
            mFieldsReviewed[index] = true;
        }
        //TODO: Else error exception
    }

    /**
     * Update an entire field
     * @param field The field that needs to be updated
     * @post mFieldsReviewed[i] = false
     */
    private void updateField(Field field) {
        int index = mFields.indexOf(field);
        if (index >= 0) {
            for (int i = 0; i < field.ROWS; i++) {
                for (int j = 0; j < field.COLUMNS; j++) {
                    mVisibleFields.get(index)[i][j] = getStatus(field, new Coordinate(i, j));
                }
            }
            mFieldsReviewed[index] = false;
        }
        //TODO: Else error exception
    }

    private CoorStatus getStatus(Field field, Coordinate coor) {
        CoorStatus status;
        if (field.isShot(coor))
            if (field.boatPlacedAt(coor))
                status = CoorStatus.HIT;
            else
                status = CoorStatus.MISSED;
        else
            status = CoorStatus.FOG;
        return status;
    }
}
