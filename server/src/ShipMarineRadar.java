/**
 * ShipMarineRadar is a medium sized 1x3 ship that can perform scans in search of enemy ships.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipMarineRadar extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 3);

    public ShipMarineRadar(Board board) {
        super(board, SIZE);
        mWeapons = new Weapon[]{new WeaponRadar(mBoard)};
    }
}
