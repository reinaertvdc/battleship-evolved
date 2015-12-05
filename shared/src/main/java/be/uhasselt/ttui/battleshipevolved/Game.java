package be.uhasselt.ttui.battleshipevolved;

import java.util.ArrayList;
import java.util.Observable;

/**
 *  The Game class, contains the main game loop
 *  @author Jelco Adamczyk
 */
public class Game extends Observable {
    private final static int maxPlayers = 4;
    private ArrayList<Player> mPlayers;
    private int mTurn;
    private Board mBoard;

    public Game() {

        //create 4 players and init
        mPlayers = new ArrayList<>();
        for (int i = 0; i < maxPlayers; i++) {
            Player player = new Player(i);
            mPlayers.add(player);
        }
        //set the turn to player 1
        mTurn = 0;
        mBoard = new Board(mPlayers.toArray(new Player[maxPlayers]));
        this.addObserver(mBoard);
        insertTestValues();
    }

    public void placementTurn() {
        Player player = mPlayers.get(mTurn);
        Coordinate co = new Coordinate(0,0);
        player.placeBattleShip(co, true);
    }

    public void nextTurn() {
        mPlayers.get(mTurn).getField().hide();
        //TODO: Refresh cooldowns
        mTurn++;
        if (mTurn >= mPlayers.size()) {
            mTurn = 0;
        }
        setChanged();
        notifyObservers(mPlayers.get(mTurn));
    }

    public void insertTestValues() {
        try {
            Ship player1Boat1 = new ShipAircraftCarrier();
            Ship player1Boat2 = new ShipPatrolBoat();
            mPlayers.get(0).getField().deployShip(player1Boat1, new Coordinate(0, 0), true);
            mPlayers.get(0).getField().deployShip(player1Boat2, new Coordinate(2, 2), false);
            Ship player2Boat1 = new ShipCruiser();
            mPlayers.get(1).getField().deployShip(player2Boat1, new Coordinate(0, 0), false);

            mPlayers.get(0).getField().shoot(new Coordinate(0, 1));
            mPlayers.get(0).getField().shoot(new Coordinate(0, 0));
            mPlayers.get(0).getField().shoot(new Coordinate(1, 1));
            mPlayers.get(0).getField().shoot(new Coordinate(1, 0));
            mPlayers.get(0).getField().shoot(new Coordinate(2, 0));
            mPlayers.get(0).getField().reveal(new Coordinate(2, 2));
            mPlayers.get(0).getField().reveal(new Coordinate(3, 3));
            nextTurn();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}
