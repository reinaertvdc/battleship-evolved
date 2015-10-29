import java.util.ArrayList;
import java.util.List;

public abstract class Ship {
    private Coordinate mSize;
    private int mHitPoints;
    protected Weapon[] mWeapons;

    protected Ship(Coordinate size) {
        mSize = size;
        mHitPoints = size.getRow() * size.getColumn();
        mWeapons = new Weapon[0];
    }

    public Coordinate getSize() {
        return mSize;
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
        return mHitPoints > 0;
    }
}
