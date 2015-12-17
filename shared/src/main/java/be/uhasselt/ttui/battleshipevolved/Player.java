package be.uhasselt.ttui.battleshipevolved;

import java.util.ArrayList;

public class Player {
    private Field mField;
    private ArrayList<Ship> mShips;
    private int mID;

    public Player(int id) {
        mField = new Field();
        mShips = new ArrayList<>();
        mID = id;
    }

    public void placeBattleShip(Coordinate coordinate, boolean liesHorizontal) {
        ShipBattleship ship = new ShipBattleship();
        try {
            mField.deployShip(ship, coordinate, liesHorizontal);
        } catch (InvalidShipPlacementException e) {
            System.err.println(e.getError());
        }
    }


    public Field getField() {
        return mField;
    }

    public int getID() {return mID;}
}
