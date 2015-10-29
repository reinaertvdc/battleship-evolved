import java.util.Observable;

public abstract class Weapon extends Observable {
    protected Board mBoard;
    private int mCooldown;
    private int mCurrentCooldown;

    public Weapon(Board board, int cooldown, int currentCooldown) {
        mBoard = board;
        mCooldown = cooldown;
        mCurrentCooldown = currentCooldown;
    }

    public void coolDown() {
        if (mCurrentCooldown > 0) {
            mCurrentCooldown--;
        }
    }

    public void deploy(Field field, Coordinate coordinate) throws NotReadyException {
        if (!isReady()) {
            throw new NotReadyException();
        } else {
            _deploy(field, coordinate);
            mCurrentCooldown = mCooldown;
        }
    }

    protected abstract void _deploy(Field field, Coordinate coordinate);

    public int getCooldown() {
        return mCooldown;
    }

    public int getCurrentCooldown() {
        return mCurrentCooldown;
    }

    public boolean isReady() {
        return mCooldown <= 0;
    }

    public class NotReadyException extends Exception {
        NotReadyException() {}
    }
}
