/**
 * ShipDecoy is a small 1x1 unarmed ship.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipDecoy extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 1);

    public ShipDecoy(Board board) {
        super(board, SIZE);
    }
}
