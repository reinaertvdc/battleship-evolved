package be.uhasselt.ttui.battleshipevolved;

/**
 * Board holds every players field
 *
 * @author Arno Stienaers
 */
public class Board {

    private Field[] fields;

    public Board(Player[] players) {
        //TODO: Find way to show what needs to be shown
        fields = new Field[players.length];

        for (int i = 0; i < players.length; i++) {
            fields[i] = players[i].getField();
        }
    }
}
