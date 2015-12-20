package be.uhasselt.ttui.battleshipevolved;

/**
 * WeaponRadar reveals a 3x3 cross and takes 3 turns to cool down.
 *
 * @author Reinaert Van de Cruys
 */
public class WeaponRadar extends Weapon {
    private static final int COOL_DOWN = 3;

    public WeaponRadar() {
        super(COOL_DOWN);
    }

    public String getName(){
        return "Scan";
    }

    /**
     * Reveals the 3x3 cross
     * @precondition given param coordinate exists on the field
     * @param fields the field object to reveal on
     * @param coordinate the center of the cross to be revealed
     */
    protected void execute(Field[] fields, Coordinate coordinate) {
        Field field = fields[0];
        //let's assume the given coordinate exists
        field.reveal(coordinate);
        //for the other coordinates, try to create them and when no indexOutOfBounds exception is thrown, we know it exists
        try {
            Coordinate top = new Coordinate(coordinate.getRow() + 1, coordinate.getColumn());
            field.reveal(top);
        } catch (IndexOutOfBoundsException e) {}
        try {
            Coordinate left = new Coordinate(coordinate.getRow(), coordinate.getColumn() - 1);
            field.reveal(left);
        } catch (IndexOutOfBoundsException e) {}
        try {
            Coordinate bot = new Coordinate(coordinate.getRow() - 1, coordinate.getColumn());
            field.reveal(bot);
        } catch (IndexOutOfBoundsException e) {}
        try {
            Coordinate right = new Coordinate(coordinate.getRow(), coordinate.getColumn() + 1);
            field.reveal(right);
        } catch (IndexOutOfBoundsException e) {}


    }
}
