package be.uhasselt.ttui.battleshipevolved;

/**
 * ShipDestroyer is a medium sized 1x3 ship that can shoot at enemy ships.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipDestroyer extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 3);

    public ShipDestroyer() {
        super(SIZE);
        mWeapons = new Weapon[]{new WeaponShot()};
    }
}
