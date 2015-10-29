public class WeaponAirStrike extends Weapon {
    private static final int COOL_DOWN = 3;

    public WeaponAirStrike(Board board) {
        super(board, COOL_DOWN, COOL_DOWN);
    }

    protected void execute(Field field, Coordinate coordinate) {
        // TODO implement method
    }
}
