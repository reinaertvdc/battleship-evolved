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
        System.out.println(words.length);
        if (words[0].compareTo("place") == 0) {
            handlePlace(words);
        } else if (words[0].compareTo("shoot") == 0) {
            handleShoot(words);
        }
        //enzoverder
    }

    /**
     * Handles the ship placement commands.
     * @param words the list of words in the command
     */
    private void handlePlace(String[] words) {
        if (words[1].compareTo("battleship") == 0) {
            Coordinate coor = new Coordinate(0, 0);
            if (coor.setFromString(words[2])) {
                mPlayer.placeBattleShip(coor, true);
            } else {
                System.out.print("Could not interpret " + words[2]);
                return;
            }
        } //enzoverder
    }

    /**
     * Handles the shooting commands
     * @param words the list of words in the command
     */
    private void handleShoot(String[] words) {
        int player = Integer.parseInt(words[1]) - 1;
        Coordinate coor = new Coordinate(0, 0);
        if (coor.setFromString(words[2])) {
            mGame.shoot(player, coor);
        } else {
            System.out.print("Could not interpret " + words[2]);
            return;
        }
    }
}
