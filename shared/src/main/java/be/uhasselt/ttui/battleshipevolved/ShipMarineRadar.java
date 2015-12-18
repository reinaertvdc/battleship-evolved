package be.uhasselt.ttui.battleshipevolved;

/**
 * ShipMarineRadar is a medium sized 1x3 ship that can perform scans in search of enemy ships.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipMarineRadar extends Ship {
    static {
        NAME = "marineradar";
        SIZE = new Coordinate(1, 3);
    }

    public ShipMarineRadar() {
        mWeapons = new Weapon[]{new WeaponRadar()};
    }
}
