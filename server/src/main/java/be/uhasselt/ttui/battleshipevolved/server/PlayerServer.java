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


    public Socket getSocket() {
        return mSocket;
    }

    public void setGame(Game game) {
        mGame = game;
        //set the players variable
        mPlayer = game.getPlayers().get(mID);
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
        System.out.println("Sent message '" + "' to client " + mID);
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
        boolean active = true;

        while (active)
        {
            String line = "";
            try {
                line = inp.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print("Received message \'" + line + " from player " + mID + "\n");
            if (line == null) {
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
        if (command.equalsIgnoreCase("place")) {
            handlePlace(words);
        } else if (command.equalsIgnoreCase("shoot")) {
            handleShoot(words);
        } else if (command.equalsIgnoreCase("scan")) {
            handleScan(words);
        } else if (command.equalsIgnoreCase("airstrike")) {
            handleAirstrike(words);
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

        //call the correct ship function
        String ship = words[1];
        String message;
        if (words[1].equalsIgnoreCase("aircraftcarrier")) {
            message = mPlayer.placeAircraftCarrier(coor, true);
        } else if (ship.equalsIgnoreCase("battleship")) {
            message = mPlayer.placeBattleShip(coor, true);
        } else if (ship.equalsIgnoreCase("cruiser")) {
            message = mPlayer.placeCruiser(coor, true);
        } else if (ship.equalsIgnoreCase("decoy")) {
            message = mPlayer.placeDecoy(coor, true);
        } else if (ship.equalsIgnoreCase("destroyer")) {
            message = mPlayer.placeDestroyer(coor, true);
        } else if (ship.equalsIgnoreCase("marineradar")) {
            message = mPlayer.placeMarineRadar(coor, true);
        } else if (ship.equalsIgnoreCase("missilecommand")) {
            message = mPlayer.placeMissileCommand(coor, true);
        } else if (ship.equalsIgnoreCase("patrolboat")) {
            message = mPlayer.placePatrolBoat(coor, true);
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
            message = mGame.shoot(player, coor);
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
}
