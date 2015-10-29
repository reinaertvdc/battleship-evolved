/**
 * ShipAircraftCarrier is a very large 1x5 ship that can perform air strikes, in addition to normal shots.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipAircraftCarrier extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 5);
    private static final int NUMBER_OF_WEAPONS = 2;

    public ShipAircraftCarrier(Board board) {
        super(board, SIZE);
        mWeapons = new Weapon[NUMBER_OF_WEAPONS];
        mWeapons[0] = new WeaponShot(mBoard);
        mWeapons[1] = new WeaponAirStrike(mBoard);
    }
}
