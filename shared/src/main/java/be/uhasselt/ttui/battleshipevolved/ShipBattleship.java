package be.uhasselt.ttui.battleshipevolved;

/**
 * ShipBattleship is a large 1x4 ship that can shoot at enemy ships.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipBattleship extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 4);

    public ShipBattleship(Board board) {
        super(board, SIZE);
        mWeapons = new Weapon[]{new WeaponShot(mBoard)};
    }
}
