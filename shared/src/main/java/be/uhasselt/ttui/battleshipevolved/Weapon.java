package be.uhasselt.ttui.battleshipevolved;

import java.util.Observable;

/**
 * Weapon is the abstract base class for all weapons.
 *
 * @author Reinaert Van de Cruys
 */
public abstract class Weapon extends Observable {
    private int mCoolDown;
    private int mCurrentCoolDown;

    protected Weapon(int coolDown) {
        mCoolDown = coolDown;
        mCurrentCoolDown = (int) Math.round(Math.random() * coolDown);
    }

    public abstract String getName();

    public void coolDown() {
        if (mCurrentCoolDown > 0) {
            mCurrentCoolDown--;
        }
    }

    public void deploy(Field[] fields, Coordinate coordinate) throws NotReadyException {
        if (!isReady()) {
            throw new NotReadyException();
        } else {
            execute(fields, coordinate);
            mCurrentCoolDown = mCoolDown;
        }
    }

    protected abstract void execute(Field[] fields, Coordinate coordinate);

    public int getCoolDown() {
        return mCoolDown;
    }

    public int getCurrentCoolDown() {
        return mCurrentCoolDown;
    }

    public boolean isReady() {
        return mCurrentCoolDown <= 0;
    }

    public class NotReadyException extends Exception {
        NotReadyException() {}
    }
}
