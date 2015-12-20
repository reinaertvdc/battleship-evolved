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
    private boolean[] mPlayersReady;
    private int mTurn;
    private Board mBoard;

    public Game() {
        //create 4 players and init
        mPlayers = new ArrayList<>();
        mPlayersReady = new boolean[maxPlayers];
        for (int i = 0; i < maxPlayers; i++) {
            Player player = new Player(i);
            mPlayers.add(player);
            mPlayersReady[i] = false;
        }
        //set the turn to player 1
        mTurn = 0;
        mBoard = new Board(mPlayers.toArray(new Player[maxPlayers]));
        this.addObserver(mBoard);
    }

    public ArrayList<Player> getPlayers() {
        return mPlayers;
    }

    /**
     * Returns whether or not the player is on turn
     */
    public boolean isOnTurn(int player) {
        return player == mTurn;
    }

    public void setPlayerReady(int PlayerID) {
        mPlayersReady[PlayerID] = true;
        boolean allPlayersReady = true;
        for (int i = 0; i < mPlayers.size() && allPlayersReady; i++)
            allPlayersReady = mPlayersReady[i];

        if (allPlayersReady) {
            setChanged();
            notifyObservers(new Boolean(true));
        }

    }

    public String nextTurn(int commander) {
        if (isOnTurn(commander)) {
            nextTurn();
            return "";
        } else {
            return "not your turn";
        }
    }

    /**
     * The function which starts the next turn
     */
    public void nextTurn() {
        for (int i = 0; i < mPlayers.size(); i++) {
            //remove all revealed spots
            Player player = mPlayers.get(i);
            player.getField().hide();

            //force the player to update wether or not they're still playing?
            //todo: maybe this is better implemented with observers so we dont have to wait till an 'end turn' event
            player.checkLoseCondition();
        }

        //refresh all cooldowns of the current player
        Player currentPlayer = mPlayers.get(mTurn);
        currentPlayer.refreshWeaponCooldowns();

        setChanged();
        //assign the next turn
        mTurn = getNextPlayer();
        if (mTurn == -1) {
            //game has end
            notifyObservers("Game Over");
        } else {
            //notify all observers
            notifyObservers(mPlayers.get(mTurn));
        }
    }

    /**
     * Functions that returns the next player
     * @return id of the next player in turn, -1 if the game is over
     */
    private int getNextPlayer() {
        int turn = mTurn;
        Player player;
        boolean noNextPlayer = false;
        do {
            turn++;
            if (turn >= mPlayers.size()) {
                turn = 0;
            }
            player = mPlayers.get(turn);
            //we have checked all other players, so the player who played the last turn is out winner!
            noNextPlayer = turn == mTurn;
        } while (!player.isPlaying() && !noNextPlayer);

        if (noNextPlayer)
            return -1;
        else
            return turn;
    }

//    public void insertTestValues() {
//        try {
//            mPlayers.get(0).placeAircraftCarrier(new Coordinate(0, 0), true);
//            mPlayers.get(0).placePatrolBoat(new Coordinate(2, 2), false);
//            mPlayers.get(1).placeCruiser(new Coordinate(0, 0), false);
//            mPlayers.get(2).placeDestroyer(new Coordinate(5, 5), false);
//
//            mPlayers.get(0).getField().shoot(new Coordinate(0, 1));
//            mPlayers.get(0).getField().shoot(new Coordinate(1, 1));
//            mPlayers.get(0).getField().shoot(new Coordinate(0, 0));
//            mPlayers.get(0).getField().shoot(new Coordinate(0, 2));
//            mPlayers.get(0).getField().shoot(new Coordinate(0, 3));
//            mPlayers.get(0).getField().shoot(new Coordinate(0, 4));
//            mPlayers.get(0).getField().shoot(new Coordinate(2, 0));
//            mPlayers.get(0).getField().reveal(new Coordinate(2, 2));
//            mPlayers.get(0).getField().reveal(new Coordinate(3, 3));
//
//            /* JELCO testdata HEHEHEHEHEHE */
//            for (int i = 0; i < mPlayers.size(); i++) {
//                mPlayers.get(i).placeBattleShip(new Coordinate(0, 0), true);
//                mPlayers.get(i).placePatrolBoat(new Coordinate(1, 0), true);
//                mPlayers.get(i).placeMarineRadar(new Coordinate(2, 0), true);
//                mPlayers.get(i).placeMissileCommand(new Coordinate(3, 0), true);
//                mPlayers.get(i).placeDestroyer(new Coordinate(4, 0), true);
//                mPlayers.get(i).placeAircraftCarrier(new Coordinate(6, 0), true);
//                mPlayers.get(i).placeDecoy(new Coordinate(7,0), true);
//            }
//
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }

    /**
     * Shoots a player on his field at coordinate coordinate
     * @param commander The playerID who is giving the command
     * @param target The playerID whose field is revealed
     * @param coordinate the coordinate to shoot at
     * @return the message with either the error or == "success"
     */
    public String shoot(int commander, int target, Coordinate coordinate) {
        if (isOnTurn(commander))
            return mPlayers.get(commander).shoot(mPlayers.get(target).getField(), coordinate);
        else
            return "not your turn";
    }

    /**
     * Reveals a cross on the field.
     * @param commander The playerID who is giving the command
     * @param target The playerID whose field is revealed
     * @param coordinate the center of the cross
     * @return the message with either the error or == "success"
     */
    public String scan(int commander, int target, Coordinate coordinate) {
        if (isOnTurn(commander))
            return mPlayers.get(commander).scan(mPlayers.get(target).getField(), coordinate);
        else
            return "not your turn";
    }

    /**
     * Shoots a 1x3 barrage at the targets field.
     * @param commander The playerID who shoots
     * @param target the playerID of the target
     * @param coordinate the coordinate of the outer left
     * @return the message with either the error or == "success"
     */
    public String airstrike(int commander, int target, Coordinate coordinate) {
        if (isOnTurn(commander))
            return mPlayers.get(commander).airstrike(mPlayers.get(target).getField(), coordinate);
        else
            return "not your turn";
    }

    /**
     * Shoots a shot at every player's field on the same coordinate
     * @param commander the player who shoots
     * @param coordinate the coordinate of the shot on each field
     * @return the message wih either the error or == "success"
     */
    public String artillery(int commander, Coordinate coordinate) {
        //Create the fields array
        Field[] fields = new Field[mPlayers.size()];
        for (int i = 0; i < mPlayers.size(); i++) {
            fields[i] = mPlayers.get(i).getField();
        }

        if (isOnTurn(commander))
            return mPlayers.get(commander).artillery(fields, coordinate);
        else
            return "not your turn";
    }

}
