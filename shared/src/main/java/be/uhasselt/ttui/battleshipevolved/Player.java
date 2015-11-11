package be.uhasselt.ttui.battleshipevolved;

import java.util.ArrayList;

public class Player {
    private Field mField;
    private ArrayList<Ship> mShips;

    public Player() {
        mField = new Field();
        mShips = new ArrayList<>();
    }

    public void placeBattleShip(Board board, Coordinate coordinate, boolean liesHorizontal) {
        ShipBattleship ship = new ShipBattleship(board);
        //mField.deployShip(ship, coordinate, liesHorizontal);
        //TODO: Catch exception
    }


    public Field getField() {
        return mField;
    }
}
