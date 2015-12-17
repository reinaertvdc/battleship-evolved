package be.uhasselt.ttui.battleshipevolved.client;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectionThread extends Service {
    public static String SERVERIP = ""; //your computer IP address should be written here
    public static final int SERVERPORT = 4004;
    private PrintWriter mOut;
    private Socket mSocket;
    private InetAddress mServerAddr;

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("I am in Ibinder onBind method");
        return myBinder;
    }

    private final IBinder myBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public ConnectionThread getService() {
            System.out.println("I am in Localbinder ");
            return ConnectionThread.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("I am in on create");
    }

    public void sendMessage(String message){
        if (mOut != null && !mOut.checkError()) {
            System.out.println("in sendMessage " + message);
            mOut.println(message);
            mOut.flush();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //super.onStartCommand(intent, flags, startId);
        String ip = intent.getExtras().getString("ip");
        Toast.makeText(this,"Connecting to " + ip + "...", Toast.LENGTH_LONG).show();
        Runnable connect = new connectSocket();
        SERVERIP = ip;
        new Thread(connect).start();
        return START_STICKY;
    }


    class connectSocket implements Runnable {
        public void sendMessage(String message) {
            mOut.println(message);
        }

        @Override
        public void run() {
            DataInputStream inp;
            try {
                mServerAddr = InetAddress.getByName(SERVERIP);
                mSocket = new Socket(mServerAddr, SERVERPORT);
                try {
                    //setup the input datastream
                    inp = new DataInputStream(mSocket.getInputStream());
                } catch (IOException e) {
                    System.out.println(e);
                    return;
                }

                try {
                    //setup the output datastream
                    mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())), true);
                    mOut.println("Hi!");
                }
                catch (Exception e) {
                    //something went wrong with sending the message to the server.
                    Log.e("TCP", "S: Error", e);
                    return;
                }
            } catch (UnknownHostException e) {
                System.out.println("Could not resolve hostname " + SERVERIP);
                return;
            } catch (IOException e) {
                //if the connection could not be made
                System.out.println("Could not connect to " + SERVERIP);
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
                System.out.println("Received message \'" + line + "\' from the server ");
                if (line == null)
                {
                    //server was terminated, so we close the client as well
                    active = false;
                    //TODO: goto MainActivity and notify user server disconnected?
                } else {
                    Intent i = new Intent("SERVER_MESSAGE");
                    i.putExtra("message", line);
                    sendBroadcast(i);
                }
                //interpretMessage(line);
            }

            //close socket and streams
            System.out.println("Closing down sockets");
            try {
                mSocket.close();
                mOut.close();
                inp.close();
            } catch (IOException e) {
                System.out.println("Could not close socket");
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mSocket.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mSocket = null;
    }

}
