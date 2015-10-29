public class ShipDecoy extends Ship {
    private static final Coordinate SIZE = new Coordinate(1, 1);

    public ShipDecoy(Board board) {
        super(board, SIZE);
    }
}
