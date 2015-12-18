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

    /*
     * Functions to place ships
     */
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
            mShips.add(ship);
            return "success";
        } catch (InvalidShipPlacementException e) {
            System.err.println(e.getError());
            return e.getError();
        }
    }

    /*
     * Functions to fire weapons
     */

    /**
     * Function to scan a field
     * @param field the field to scan
     * @return the message with either the error or == "success"
     */
    public String scan(Field field, Coordinate coordinate) {
        for (int i = 0; i < mShips.size(); i++) {
            Ship ship = mShips.get(i);
            if (ship instanceof ShipMarineRadar) {
                ShipMarineRadar radar = (ShipMarineRadar) ship;

                if (radar.isSunk()) {
                    return "marineradar ship has sunk";
                }

                Weapon[] weapons = radar.getWeapons();
                for (int j = 0; j < weapons.length; j++) {
                    Weapon weapon = weapons[j];
                    if (weapon instanceof WeaponRadar) {
                        WeaponRadar weaponRadar = (WeaponRadar) weapon;
                        try {
                            weaponRadar.deploy(field, coordinate);
                            return "success";
                        } catch (Weapon.NotReadyException e) {
                            return "radar is on cooldown";
                        }
                    }
                }
            }
        }
        //if no radar ship is found, give the error
        return "no marineradar ship found with a radar on board";
    }

    public String shoot(Field field, Coordinate coordinate) {
        boolean foundWeapon = false;
        for (int i = 0; i < mShips.size(); i++) {
            Ship ship = mShips.get(i);
            if (ship.isSunk()) {
                return "ship has sunk";
            }

            Weapon[] weapons = ship.getWeapons();
            for (int j = 0; j < weapons.length; j++) {
                Weapon weapon = weapons[j];
                if (weapon instanceof WeaponShot) {
                    foundWeapon = true;
                    WeaponShot weaponShot = (WeaponShot) weapon;
                    try {
                        weaponShot.deploy(field, coordinate);
                        return "success";
                    } catch (Weapon.NotReadyException e) {
                        continue;
                    }
                }
            }
        }
        //if no radar ship is found, give the error
        if (foundWeapon)
            return "All shots are on cooldown";
        else
            return "no ship found with a radar on board";
    }

    public String airstrike(Field field, Coordinate coordinate) {
        for (int i = 0; i < mShips.size(); i++) {
            Ship ship = mShips.get(i);
            if (ship instanceof ShipAircraftCarrier) {
                ShipAircraftCarrier carrier = (ShipAircraftCarrier) ship;

                if (carrier.isSunk()) {
                    return "aircraftcarrier ship has sunk";
                }

                Weapon[] weapons = carrier.getWeapons();
                for (int j = 0; j < weapons.length; j++) {
                    Weapon weapon = weapons[j];
                    if (weapon instanceof WeaponAirStrike) {
                        WeaponAirStrike weaponAirStrike = (WeaponAirStrike) weapon;
                        try {
                            weaponAirStrike.deploy(field, coordinate);
                            return "success";
                        } catch (Weapon.NotReadyException e) {
                            return "airstrike is on cooldown";
                        }
                    }
                }
            }
        }
        //if no radar ship is found, give the error
        return "no aircraftcarrier ship found with a radar on board";
    }

    public Field getField() {
        return mField;
    }

    public int getID() {return mID;}
}
