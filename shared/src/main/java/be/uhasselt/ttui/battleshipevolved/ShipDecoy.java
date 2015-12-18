package be.uhasselt.ttui.battleshipevolved;

/**
 * ShipDecoy is a small 1x1 unarmed ship.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipDecoy extends Ship {
    static {
        NAME = "Decoy";
        SIZE = new Coordinate(1, 1);
    }

    //This ship doesn't sink
    public boolean isSunk() {
        return false;
    }
}
