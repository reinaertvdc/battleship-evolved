package be.uhasselt.ttui.battleshipevolved;

/**
 * Ship is the abstract base class for all ships.
 *
 * @author Reinaert Van de Cruys
 */
public abstract class Ship {
    protected static String NAME;
    protected static Coordinate SIZE;
    protected Weapon[] mWeapons = new Weapon[0];
    private int mHitPoints = SIZE.getRow() * SIZE.getColumn();

    public static Coordinate getSize() {
        return SIZE;
    }

    public Weapon[] getWeapons() {
        return mWeapons;
    }

    public void hit() {
        if (mHitPoints > 0) {
            mHitPoints--;
        }
    }

    public boolean isSunk() {
        return mHitPoints <= 0;
    }

    public static String getName() {
        return NAME;
    }
}
