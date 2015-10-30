package be.uhasselt.ttui.battleshipevolved;

/**
 * ShipPatrolBoat is a small 1x2 ship that can shoot at enemy ships.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipPatrolBoat extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 2);

    public ShipPatrolBoat(Board board) {
        super(board, SIZE);
        mWeapons = new Weapon[]{new WeaponShot(mBoard)};
    }
}
