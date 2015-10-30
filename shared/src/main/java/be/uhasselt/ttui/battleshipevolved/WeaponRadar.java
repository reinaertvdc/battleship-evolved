package be.uhasselt.ttui.battleshipevolved;

/**
 * WeaponRadar reveals a 1x3 cross and takes 3 turns to cool down.
 *
 * @author Reinaert Van de Cruys
 */
public class WeaponRadar extends Weapon {
    private static final int COOL_DOWN = 3;

    public WeaponRadar(Board board) {
        super(board, COOL_DOWN, COOL_DOWN);
    }

    protected void execute(Field field, Coordinate coordinate) {
        // TODO implement method
    }
}
