/**
 * ShipBattleship is a large 1x4 ship that can shoot at enemy ships.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipBattleship extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 4);
    private static final int NUMBER_OF_WEAPONS = 1;

    public ShipBattleship(Board board) {
        super(board, SIZE);
        mWeapons = new Weapon[NUMBER_OF_WEAPONS];
        mWeapons[0] = new WeaponShot(mBoard);
    }
}
