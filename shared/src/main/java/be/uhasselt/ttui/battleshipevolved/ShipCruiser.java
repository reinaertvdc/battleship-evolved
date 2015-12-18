package be.uhasselt.ttui.battleshipevolved;

/**
 * ShipCruiser is a medium sized 1x3 ship that can shoot at enemy ships.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipCruiser extends Ship {
    static {
        NAME = "Cruiser";
        SIZE = new Coordinate(1, 3);
    }

    public ShipCruiser() {
        mWeapons = new Weapon[]{new WeaponShot()};
    }
}
