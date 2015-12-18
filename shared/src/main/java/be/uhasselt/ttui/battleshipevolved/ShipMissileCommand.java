package be.uhasselt.ttui.battleshipevolved;

/**
 * ShipMissileCommand is a large 2x2 ship that can call for artillery.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipMissileCommand extends Ship {
    static {
        NAME = "MissileCommand";
        SIZE = new Coordinate(2, 2);
    }

    public ShipMissileCommand() {
        mWeapons = new Weapon[]{new WeaponArtillery()};
    }
}
