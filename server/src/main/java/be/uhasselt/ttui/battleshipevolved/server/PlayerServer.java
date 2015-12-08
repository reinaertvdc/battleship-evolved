package be.uhasselt.ttui.battleshipevolved.server;

import java.net.Socket;

/**
 * A thread that handles everything from a single player
 * @author Arno Stienaers
 */
public class PlayerServer extends Thread {
    private Socket mSocket = null;
    private int mID;

    public Socket getSocket() {
        return mSocket;
    }
    public PlayerServer(Socket socket, int id) {
        mSocket = socket;
        mID = id;
    }

    public void run() {

    }
}
