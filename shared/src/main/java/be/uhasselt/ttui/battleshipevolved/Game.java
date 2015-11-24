package be.uhasselt.ttui.battleshipevolved;

import java.util.ArrayList;

/**
 *  The Game class, contains the main game loop
 *  @author Jelco Adamczyk
 */
public class Game {
    private final static int maxPlayers = 4;
    private ArrayList<Player> mPlayers;
    private int mTurn;

    public Game() {

        //create 4 players and init
        mPlayers = new ArrayList<>();
        for (int i = 0; i < maxPlayers; i++) {
            Player player = new Player();
            mPlayers.add(player);
        }
        //set the turn to player 1
        mTurn = 0;
    }

    public void placementTurn() {
        Player player = mPlayers.get(mTurn);
        Coordinate co = new Coordinate(0,0);
        player.placeBattleShip(co, true);
    }


}
