/**
 * ShipCruiser is a medium sized 1x3 ship that can shoot at enemy ships.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipCruiser extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 3);

    public ShipCruiser(Board board) {
        super(board, SIZE);
        mWeapons = new Weapon[]{new WeaponShot(mBoard)};
    }
}
