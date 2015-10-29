import java.util.Observable;

public abstract class Weapon extends Observable {
    private int mCooldown;
    private int mCurrentCooldown;
    protected Board mBoard;

    protected Weapon(Board board, int cooldown, int currentCooldown) {
        mCooldown = cooldown;
        mCurrentCooldown = currentCooldown;
        mBoard = board;
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
            execute(field, coordinate);
            mCurrentCooldown = mCooldown;
        }
    }

    protected abstract void execute(Field field, Coordinate coordinate);

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
