package be.uhasselt.ttui.battleshipevolved;

/**
 * ShipBattleship is a large 1x4 ship that can shoot at enemy ships.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipBattleship extends Ship {
    static {
        NAME = "Battleship";
        SIZE = new Coordinate(1, 4);
    }

    public ShipBattleship() {
        mWeapons = new Weapon[]{new WeaponShot()};
    }
}
