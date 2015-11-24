package be.uhasselt.ttui.battleshipevolved;

/**
 * WeaponAirStrike hits a 1x3 area and takes 3 turns to cool down.
 *
 * @author Reinaert Van de Cruys
 */
public class WeaponAirStrike extends Weapon {
    private static final int COOL_DOWN = 3;

    public WeaponAirStrike() {
        super(COOL_DOWN, COOL_DOWN);
    }

    protected void execute(Field field, Coordinate coordinate) {
        // TODO implement method
    }
}
