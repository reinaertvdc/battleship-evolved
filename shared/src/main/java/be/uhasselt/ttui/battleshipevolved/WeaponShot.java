package be.uhasselt.ttui.battleshipevolved;

/**
 * WeaponRadar hits a single coordinate and takes 1 turn to cool down.
 *
 * @author Reinaert Van de Cruys
 */
public class WeaponShot extends Weapon {
    private static final int COOL_DOWN = 1;

    public WeaponShot() {
        /** TODO: TEMPORARY FIX */
        super(COOL_DOWN, 0);
        //super(COOL_DOWN, COOL_DOWN);
    }

    protected void execute(Field[] fields, Coordinate coordinate) {
        Field field = fields[0];
        field.shoot(coordinate);
    }
}
