public class ShipDestroyer extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 2);
    private static final int NUMBER_OF_WEAPONS = 1;

    public ShipDestroyer(Board board) {
        super(board, SIZE);
        mWeapons = new Weapon[NUMBER_OF_WEAPONS];
        mWeapons[0] = new WeaponShot(mBoard);
    }
}
