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

    public String placeAircraftCarrier(Coordinate coordinate, boolean liesHorizontal) {
        return placeShip(new ShipAircraftCarrier(), coordinate, liesHorizontal);
    }
    public String placeBattleShip(Coordinate coordinate, boolean liesHorizontal) {
        return placeShip(new ShipBattleship(), coordinate, liesHorizontal);
    }
    public String placeCruiser(Coordinate coordinate, boolean liesHorizontal) {
        return placeShip(new ShipCruiser(), coordinate, liesHorizontal);
    }
    public String placeDecoy(Coordinate coordinate, boolean liesHorizontal) {
        return placeShip(new ShipDecoy(), coordinate, liesHorizontal);
    }
    public String placeDestroyer(Coordinate coordinate, boolean liesHorizontal) {
        return placeShip(new ShipDestroyer(), coordinate, liesHorizontal);
    }
    public String placeMarineRadar(Coordinate coordinate, boolean liesHorizontal) {
        return placeShip(new ShipMarineRadar(), coordinate, liesHorizontal);
    }
    public String placeMissileCommand(Coordinate coordinate, boolean liesHorizontal) {
        return placeShip(new ShipMissileCommand(), coordinate, liesHorizontal);
    }
    public String placePatrolBoat(Coordinate coordinate, boolean liesHorizontal) {
        return placeShip(new ShipPatrolBoat(), coordinate, liesHorizontal);
    }

    private String placeShip(Ship ship, Coordinate coordinate, boolean liesHorizontal) {
        try {
            mField.deployShip(ship, coordinate, liesHorizontal);
            return "success";
        } catch (InvalidShipPlacementException e) {
            System.err.println(e.getError());
            return e.getError();
        }
    }


    public Field getField() {
        return mField;
    }

    public int getID() {return mID;}
}
