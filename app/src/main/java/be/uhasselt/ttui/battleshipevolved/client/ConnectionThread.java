package be.uhasselt.ttui.battleshipevolved.client;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionThread extends Service {
    public static final String SERVERIP = ""; //your computer IP address should be written here
    public static final int SERVERPORT = 5000;
    PrintWriter mOut;
    Socket mSocket;
    InetAddress mServerAddr;

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

    public void IsBoundable(){
        Toast.makeText(this, "I bind like butter", Toast.LENGTH_LONG).show();
    }

    public void sendMessage(String message){
        if (mOut != null && !mOut.checkError()) {
            System.out.println("in sendMessage " + message);
            mOut.println(message);
            mOut.flush();
        }
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        System.out.println("I am in on start");
        //Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        Runnable connect = new connectSocket();
        new Thread(connect).start();
        return START_STICKY;
    }

    class connectSocket implements Runnable {
        @Override
        public void run() {
            try {
                //here you must put your computer's IP address.
                mServerAddr = InetAddress.getByName(SERVERIP);
                Log.e("TCP Client", "C: Connecting...");
                //create a socket to make the connection with the server
                mSocket = new Socket(mServerAddr, SERVERPORT);
                try {
                    //send the message to the server
                    mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())), true);
                    Log.e("TCP Client", "C: Sent.");
                    Log.e("TCP Client", "C: Done.");
                }
                catch (Exception e) {
                    Log.e("TCP", "S: Error", e);
                }
            } catch (Exception e) {
                Log.e("TCP", "C: Error", e);
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
