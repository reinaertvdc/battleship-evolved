package be.uhasselt.ttui.battleshipevolved.server;

import be.uhasselt.ttui.battleshipevolved.Coordinate;
import be.uhasselt.ttui.battleshipevolved.Game;
import be.uhasselt.ttui.battleshipevolved.Player;
import com.sun.org.apache.xerces.internal.xs.StringList;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A thread that handles everything from a single player
 */
public class PlayerServer extends Thread {
    private Socket mSocket = null;
    PrintStream mOutput = null;
    private int mID;
    private Game mGame = null;
    private Player mPlayer = null;
    private boolean active;

    public void shutdown() {
        active = false;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void setGame(Game game) {
        mGame = game;
        //set the players variable
        mPlayer = game.getPlayers().get(mID);
        sendMessage("game start");
    }

    public PlayerServer(Socket socket, int id) {
        mSocket = socket;
        mID = id;
        try {
            mOutput = new PrintStream(mSocket.getOutputStream());
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public void sendMessage(String message) {
        mOutput.println(message);
        System.out.println("Sent message '" + message + "' to client " + mID);
    }

    public void run() {
        DataInputStream inp;
        try {
            inp = new DataInputStream(mSocket.getInputStream());
        }
        catch (IOException e) {
            System.out.println(e);
            return;
        }
        active = true;

        while (active)
        {
            String line = "";
            try {
                line = inp.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print("Received message \'" + line + "\' from player " + mID + "\n");
            if (line == null || !active) {
                //client disconnected
                active = false;
                //TODO (maybe) allow the client to reconnect?
            } else {
                interpretMessage(line);
            }
        }

        //close streams and socket
        try {
            mOutput.close();
            inp.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interprets a message given by a client and calls the appropriate function
     * @param message The message given by the client
     */
    private void interpretMessage(String message) {
        String[] words = message.split(" ");
        String command = words[0];
        if (message.equalsIgnoreCase("Hi!")) {
            //connection was made. Return ack
            sendMessage("Hello!");
        } else if (command.equalsIgnoreCase("place")) {
            handlePlace(words);
        } else if (command.equalsIgnoreCase("shoot")) {
            handleShoot(words);
        } else if (command.equalsIgnoreCase("scan")) {
            handleScan(words);
        } else if (command.equalsIgnoreCase("airstrike")) {
            handleAirstrike(words);
        } else if (command.equalsIgnoreCase("artillery")) {
            handleArtillery(words);
        } else if (message.equalsIgnoreCase("end turn")) {
            handleEndTurn();
        } else if (message.equalsIgnoreCase("Send cooldowns")) {
            sendCooldowns();
        } else if (message.equalsIgnoreCase("Send turn")) {
            sendCooldowns();
        } else if (message.equalsIgnoreCase("Send positions")) {
            sendPositions();
        }

        else {
            sendMessage("Could not interpret " + command);
        }
    }

    /**
     * Handles the ship placement commands.
     * @param words the list of words in the command
     */
    private void handlePlace(String[] words) {
        //first interpret the given coordinate
        Coordinate coor = new Coordinate(0, 0);
        if (!coor.setFromString(words[2])) {
            sendMessage("Could not interpret " + words[2]);
            return;
        }

        //determine vertical or horizontal position
        boolean isHorizontal;
        if (words[3].equalsIgnoreCase("horizontal"))
            isHorizontal = true;
        else if (words[3].equalsIgnoreCase("vertical")) {
            isHorizontal = false;
        } else
            return;

        //call the correct ship function
        String ship = words[1];
        String message;
        if (words[1].equalsIgnoreCase("aircraftcarrier")) {
            message = mPlayer.placeAircraftCarrier(coor, isHorizontal);
        } else if (ship.equalsIgnoreCase("battleship")) {
            message = mPlayer.placeBattleShip(coor, isHorizontal);
        } else if (ship.equalsIgnoreCase("cruiser")) {
            message = mPlayer.placeCruiser(coor, isHorizontal);
        } else if (ship.equalsIgnoreCase("decoy")) {
            message = mPlayer.placeDecoy(coor, isHorizontal);
        } else if (ship.equalsIgnoreCase("destroyer")) {
            message = mPlayer.placeDestroyer(coor, isHorizontal);
        } else if (ship.equalsIgnoreCase("marineradar")) {
            message = mPlayer.placeMarineRadar(coor, isHorizontal);
        } else if (ship.equalsIgnoreCase("missilecommand")) {
            message = mPlayer.placeMissileCommand(coor, isHorizontal);
        } else if (ship.equalsIgnoreCase("patrolboat")) {
            message = mPlayer.placePatrolBoat(coor, isHorizontal);
        } else {
            message = "Could not interpret " + ship;
        }
        sendMessage(message);
    }

    /**
     * Handles the shooting commands
     * @param words the list of words in the command
     */
    private void handleShoot(String[] words) {
        String message;
        int player = Integer.parseInt(words[1]) - 1;
        Coordinate coor = new Coordinate(0, 0);
        if (coor.setFromString(words[2])) {
            message = mGame.shoot(mID, player, coor);
        } else {
            message = "Could not interpret " + words[2];
        }
        sendMessage(message);
    }

    /**
     * Handles the scan command
     */
    private void handleScan(String[] words) {
        String message;
        int player = Integer.parseInt(words[1]) - 1;
        Coordinate coor = new Coordinate(0, 0);
        if (coor.setFromString(words[2])) {
            message = mGame.scan(mID, player, coor);
        } else {
            message = "Could not interpret " + words[2];
        }
        sendMessage(message);
    }

    /**
     * Handles the airstrike command
     */
    private void handleAirstrike(String[] words) {
        String message;
        int player = Integer.parseInt(words[1]) - 1;
        Coordinate coor = new Coordinate(0, 0);
        if (coor.setFromString(words[2])) {
            message = mGame.airstrike(mID, player, coor);
        } else {
            message = "Could not interpret " + words[2];
        }
        sendMessage(message);
    }

    /**
     * Handles the artillery command
     */
    private void handleArtillery(String[] words) {
        String message;
        Coordinate coor = new Coordinate(0, 0);
        if (coor.setFromString(words[1])) {
            message = mGame.artillery(mID, coor);
        } else {
            message = "Could not interpret " + words[1];
        }
        sendMessage(message);
    }

    /**
     * Handles the end turn command
     */
    private void handleEndTurn() {
        String message = mGame.nextTurn(mID);
        if (!message.isEmpty())
            sendMessage(message);
    }

    /**
     * Send cooldowns
     */
    private void sendCooldowns() {
        ArrayList<String> cooldowns = mPlayer.getCooldowns();
        for (int i = 0; i < cooldowns.size(); i++){
            sendMessage("Cooldown " + cooldowns.get(i));
        }
    }

    /**
     * Send ship positions
     */
    private void sendPositions() {
        ArrayList<Coordinate> coors = mPlayer.getField().giveAllBoatsPos();
        for (int i = 0; i < coors.size(); i++) {
            sendMessage("Position " + coors.get(i).getRow() + " " + coors.get(i).getColumn());
        }
    }

    /**
     * Send turn
     */
    private void sendTurn(){
        ArrayList<Player> players = mGame.getPlayers();
        for (int i = 0; i < players.size(); i++){
            if (mGame.isOnTurn(players.get(i).getID())){
                if (i == mPlayer.getID())
                    sendMessage("your turn");
                else
                    sendMessage("next turn for player " + (players.get(i).getID()));
            }
        }
    }
}
