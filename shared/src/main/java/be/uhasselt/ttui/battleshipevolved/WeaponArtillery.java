package be.uhasselt.ttui.battleshipevolved;

/**
 * WeaponArtillery hits the same coordinate on all fields and takes 3 turns to cool down.
 *
 * @author Reinaert Van de Cruys
 */
public class WeaponArtillery extends Weapon {
    private static final int COOL_DOWN = 3;

    public WeaponArtillery() {
        /** TODO: TEMPORARY FIX */
        super(COOL_DOWN, 0);
        //super(COOL_DOWN, COOL_DOWN);
    }

    protected void execute(Field[] fields, Coordinate coordinate) {
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.shoot(coordinate);
        }
    }
}
