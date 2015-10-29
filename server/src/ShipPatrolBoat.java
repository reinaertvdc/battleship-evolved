/**
 * ShipPatrolBoat is a small 1x2 ship that can shoot at enemy ships.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipPatrolBoat extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 2);
    private static final int NUMBER_OF_WEAPONS = 1;

    public ShipPatrolBoat(Board board) {
        super(board, SIZE);
        mWeapons = new Weapon[NUMBER_OF_WEAPONS];
        mWeapons[0] = new WeaponShot(mBoard);
    }
}
