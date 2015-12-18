package be.uhasselt.ttui.battleshipevolved;

/**
 * WeaponAirStrike hits a 1x3 area and takes 3 turns to cool down.
 *
 * @author Reinaert Van de Cruys
 */
public class WeaponAirStrike extends Weapon {
    private static final int COOL_DOWN = 3;

    public WeaponAirStrike() {
        /** TODO: TEMPORARY FIX */
        super(COOL_DOWN, 0);
        //super(COOL_DOWN, COOL_DOWN);
    }

    /**
     * Shoots the 1x3 area
     * @param field the field to use the airstrike on
     * @param coordinate the outer left coordinate of the airstrike
     */
    protected void execute(Field field, Coordinate coordinate) {
        Coordinate mid = new Coordinate(coordinate.getRow(), coordinate.getColumn() + 1);
        Coordinate right = new Coordinate(coordinate.getRow(), coordinate.getColumn() + 2);
        field.shoot(coordinate);
        field.shoot(mid);
        field.shoot(right);
    }
}
