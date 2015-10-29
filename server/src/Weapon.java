import java.util.Observable;

/**
 * Weapon is the abstract base class for all weapons.
 *
 * @author Reinaert Van de Cruys
 */
public abstract class Weapon extends Observable {
    private int mCoolDown;
    private int mCurrentCoolDown;
    protected Board mBoard;

    protected Weapon(Board board, int coolDown, int currentCoolDown) {
        mCoolDown = coolDown;
        mCurrentCoolDown = currentCoolDown;
        mBoard = board;
    }

    public void coolDown() {
        if (mCurrentCoolDown > 0) {
            mCurrentCoolDown--;
        }
    }

    public void deploy(Field field, Coordinate coordinate) throws NotReadyException {
        if (!isReady()) {
            throw new NotReadyException();
        } else {
            execute(field, coordinate);
            mCurrentCoolDown = mCoolDown;
        }
    }

    protected abstract void execute(Field field, Coordinate coordinate);

    public int getCoolDown() {
        return mCoolDown;
    }

    public int getCurrentCooldown() {
        return mCurrentCoolDown;
    }

    public boolean isReady() {
        return mCoolDown <= 0;
    }

    public class NotReadyException extends Exception {
        NotReadyException() {}
    }
}
