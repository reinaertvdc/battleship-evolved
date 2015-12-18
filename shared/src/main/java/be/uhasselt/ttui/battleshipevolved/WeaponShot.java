package be.uhasselt.ttui.battleshipevolved;

/**
 * WeaponRadar hits a single coordinate and takes 1 turn to cool down.
 *
 * @author Reinaert Van de Cruys
 */
public class WeaponShot extends Weapon {
    private static final int COOL_DOWN = 1;

    public WeaponShot() {
        super(COOL_DOWN, COOL_DOWN);
    }

    protected void execute(Field field, Coordinate coordinate) {
        field.shoot(coordinate);
    }
}
