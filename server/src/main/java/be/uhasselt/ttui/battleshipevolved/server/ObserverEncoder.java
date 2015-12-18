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
    Game mGame;

    public ObserverEncoder (Game game, ArrayList<PlayerServer> servers){
        mClients = servers;
        mGame = game;
        attachToObservables();
    }

    //TODO: Attach to cooldowns
    private void attachToObservables(){
        ArrayList<Player> players = mGame.getPlayers();
        for (int i = 0; i < players.size(); i++){
            players.get(i).getField().addObserver(this);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        //int playerID = findOriginPlayer(o);
        if (arg instanceof CoordinateStatus) {
            encodeCoordinateStatus((CoordinateStatus) arg);
        } else if (arg instanceof ArrayList<?>) {
            for (Object obj : (ArrayList<?>) arg)
                if (obj instanceof CoordinateStatus)
                    encodeCoordinateStatus((CoordinateStatus) obj);
        }
    }

    private int findOriginPlayer(Observable o) {
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


    private String encodeCoordinateStatus(CoordinateStatus cs){
        return "CoordinateUpdate " + cs.getRow() + " " + cs.getColumn() + " " + cs.getStatus();
    }
}
