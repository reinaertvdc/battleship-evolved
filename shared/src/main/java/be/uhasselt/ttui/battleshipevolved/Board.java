package be.uhasselt.ttui.battleshipevolved;

import java.util.ArrayList;

/**
 * Board holds every players field
 *
 * @author Arno Stienaers
 */
public class Board {
    private ArrayList<Field> mFields;

    public Board(ArrayList<Field> fields) {
        //TODO: Find way to show what needs to be shown
        mFields = fields;
    }
}
