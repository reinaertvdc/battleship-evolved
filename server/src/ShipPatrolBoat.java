public class ShipPatrolBoat extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 2);

    public ShipPatrolBoat(Board board) {
        super(board, SIZE);
        mWeapons = new Weapon[1];
        mWeapons[0] = new WeaponShot(mBoard);
    }
}
