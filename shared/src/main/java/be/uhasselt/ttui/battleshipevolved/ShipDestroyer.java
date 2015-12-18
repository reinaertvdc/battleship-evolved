package be.uhasselt.ttui.battleshipevolved;

/**
 * ShipDestroyer is a medium sized 1x3 ship that can shoot at enemy ships.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipDestroyer extends Ship {
    static {
        NAME = "Destroyer";
        SIZE = new Coordinate(1, 3);
    }

    public ShipDestroyer() {
        mWeapons = new Weapon[]{new WeaponShot()};
    }
}
