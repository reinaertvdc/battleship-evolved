package be.uhasselt.ttui.battleshipevolved.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * A thread that handles everything from a single player
 */
public class PlayerServer extends Thread {
    private Socket mSocket = null;
    PrintStream mOutput = null;
    private int mID;

    public Socket getSocket() {
        return mSocket;
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
}
