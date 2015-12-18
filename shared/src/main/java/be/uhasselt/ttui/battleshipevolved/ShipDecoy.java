package be.uhasselt.ttui.battleshipevolved;

/**
 * ShipDecoy is a small 1x1 unarmed ship.
 *
 * @author Reinaert Van de Cruys
 */
public class ShipDecoy extends Ship {
    static {
        NAME = "decoy";
        SIZE = new Coordinate(1, 1);
    }
}
