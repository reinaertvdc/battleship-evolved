public class WeaponShot extends Weapon {
    private static final int COOLDOWN = 1;

    public WeaponShot(Board board) {
        super(board, COOLDOWN, COOLDOWN);
    }

    protected void execute(Field field, Coordinate coordinate) {
        // TODO implement method
    }
}
