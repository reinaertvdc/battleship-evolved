package be.uhasselt.ttui.battleshipevolved;

import java.util.Random;

/**
 * WeaponRadar hits a single coordinate and takes 1 turn to cool down.
 *
 * @author Reinaert Van de Cruys
 */
public class WeaponShot extends Weapon {
    private static final int COOL_DOWN = 2; //change to 0 to shoot unlimited :D
    Random rand = new Random();

    public WeaponShot() {
        super(COOL_DOWN);
    }

    public String getName(){
        return "Bomb";
    }

    protected void execute(Field[] fields, Coordinate coordinate) {
        Field field = fields[0];
        field.shoot(coordinate);
    }
}
