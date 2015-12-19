package be.uhasselt.ttui.battleshipevolved.server;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import be.uhasselt.ttui.battleshipevolved.CoordinateStatus;
import be.uhasselt.ttui.battleshipevolved.Field;
import be.uhasselt.ttui.battleshipevolved.Game;
import be.uhasselt.ttui.battleshipevolved.Player;

/**
 * Created by Arno on 18/12/2015.
 */
public class ObserverEncoder implements Observer {
    ArrayList<PlayerServer> mClients;
    boolean[] mConnected;
    Game mGame;

    public ObserverEncoder (Game game, ArrayList<PlayerServer> servers){
        mClients = servers;
        mGame = game;
        mConnected = new boolean[4];
        mConnected[0] = true;
        mConnected[1] = true;
        mConnected[2] = true;
        mConnected[3] = true;
        attachToObservables();
    }

    //TODO: Attach to cooldowns
    private void attachToObservables(){
        ArrayList<Player> players = mGame.getPlayers();
        for (int i = 0; i < players.size(); i++){
            players.get(i).getField().addObserver(this);
        }
        mGame.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof CoordinateStatus) {
            sendUpdate(findFieldOriginPlayer(o), (CoordinateStatus) arg);
        } else if (arg instanceof ArrayList<?>) {
            for (Object obj : (ArrayList<?>) arg) {
                if (obj instanceof CoordinateStatus)
                    sendUpdate(findFieldOriginPlayer(o), (CoordinateStatus) obj);
                else if (obj instanceof String){
                    //TODO: handle cooldowns
                }
            }
        }
        else if (arg instanceof Player) {
            //next turn was called
            for (int i = 0; i < mClients.size(); i++) {
                PlayerServer server = mClients.get(i);
                //first check if any player has left the game
                ArrayList<Player> players = mGame.getPlayers();
                for (int j = 0; j < players.size(); j++)
                    if (!players.get(j).isPlaying() && mConnected[j] == true) {
                        mConnected[j] = false;
                        for (int k = 0; k < mClients.size(); k++)
                            mClients.get(k).sendMessage("player " + (j+1) + " has left the game");
                    }
                Player player = (Player)arg;
                server.sendMessage("next turn for player " + (player.getID()+1));
            }
        } else if (arg instanceof String) {
            String str = (String)arg;
            if (str.equalsIgnoreCase("Game Over")) {
                //game has ended
                for (int i = 0; i < mClients.size(); i++) {
                    PlayerServer server = mClients.get(i);
                    server.sendMessage("game has ended");
                    server.shutdown();
                }
            }
        }
    }

    private int findFieldOriginPlayer(Observable o) {
        if (o instanceof Field) {
            Field f = (Field) o;
            ArrayList<Player> players = mGame.getPlayers();
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getField() == f)
                    return i;
            }
        }
        return -1;
    }

    private void sendUpdate(int player, CoordinateStatus cs) {
        if (player >= 0 && player < mClients.size()) {
            if (cs.getStatus() == CoordinateStatus.Status.HIT ||
                    cs.getStatus() == CoordinateStatus.Status.MISSED) {
                mClients.get(player).sendMessage(encodeCoordinateStatus(cs));
            }
        }
    }

    private String encodeCoordinateStatus(CoordinateStatus cs){
        return "CoordinateUpdate " + cs.getRow() + " " + cs.getColumn() + " " + cs.getStatus();
    }
}
