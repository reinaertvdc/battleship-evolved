package be.uhasselt.ttui.battleshipevolved;

/**
 * Simple datapassing coordinate class with status
 * @Author Arno Stienaers
 */
public class CoordinateStatus extends Coordinate {
    public enum Status {FOG, MISSED, HIT, BOAT};

    private Status mStatus;

    public CoordinateStatus(int row, int column, Status status) {
        super(row, column);
        mStatus = status;
    }

    public CoordinateStatus(Coordinate coordinate, Status status) {
        super(coordinate.getRow(), coordinate.getColumn());
        mStatus = status;
    }

    public Status getStatus() { return mStatus; }
}
