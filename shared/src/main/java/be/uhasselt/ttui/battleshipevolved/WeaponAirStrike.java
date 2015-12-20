package be.uhasselt.ttui.battleshipevolved;

/**
 * WeaponAirStrike hits a 1x3 area and takes 3 turns to cool down.
 *
 * @author Reinaert Van de Cruys
 */
public class WeaponAirStrike extends Weapon {
    private static final int COOL_DOWN = 5;

    public WeaponAirStrike() {
        super(COOL_DOWN);
    }

    public String getName(){
        return "Airstrike";
    }

    /**
     * Shoots the 1x3 area
     * @param fields the field to use the airstrike on
     * @param coordinate the outer left coordinate of the airstrike
     */
    protected void execute(Field[] fields, Coordinate coordinate) {
        Field field = fields[0];
        Coordinate mid = new Coordinate(coordinate.getRow(), coordinate.getColumn() + 1);
        Coordinate right = new Coordinate(coordinate.getRow(), coordinate.getColumn() + 2);
        field.shoot(coordinate);
        field.shoot(mid);
        field.shoot(right);
    }
}
