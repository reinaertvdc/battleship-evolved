package be.uhasselt.ttui.battleshipevolved;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.sound.sampled.Clip;

/**
 * Created by Arno on 17/12/2015.
 */
public class Audio implements Observer {
    private static final String RESOURCE_PATH =
            System.getProperty("user.dir") + "/server/src/main/java/be/uhasselt/" +
                    "ttui/battleshipevolved/server/resources/audio/";

    private ArrayList<String> mAudioHitPaths;
    private ArrayList<String> mAudioWaterPaths;
    private ArrayList<String> mAudioDiscoveredPaths;
    private ArrayList<String> mAudioShipSunkPaths;

    public Audio() {
        mAudioHitPaths = new ArrayList<String>();
        mAudioHitPaths.add(RESOURCE_PATH + "Missile1.wav");
        mAudioWaterPaths = new ArrayList<String>();
        mAudioWaterPaths.add(RESOURCE_PATH + "Water1.wav");
        mAudioDiscoveredPaths = new ArrayList<String>();
        mAudioDiscoveredPaths.add(RESOURCE_PATH + "Hint.wav");
        mAudioDiscoveredPaths = new ArrayList<String>();
        mAudioDiscoveredPaths.add(RESOURCE_PATH + "BattleShipDeath1.wav");
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof CoordinateStatus) {
            CoordinateStatus cStatus = (CoordinateStatus) arg;
            playSound(cStatus.getStatus());
        } else if (arg instanceof ArrayList<?>) {
            for (Object obj : (ArrayList<?>) arg)
                if (obj instanceof CoordinateStatus) {
                    CoordinateStatus cStatus = (CoordinateStatus) obj;
                    playSound(cStatus.getStatus());
                }
        }/* else if (arg == true) {
            AudioPlayer player = new AudioPlayer();
            player.play(mAudioShipSunkPaths.get(0));
        }*/
    }

    private void playSound(CoordinateStatus.Status type) {
        AudioPlayer player = new AudioPlayer();
        switch (type) {
            case FOG:
                break;
            case HIT:
                player.play(mAudioHitPaths.get(0));
                break;
            case MISSED:
                player.play(mAudioWaterPaths.get(0));
                break;
            case BOAT:
                player.play(mAudioDiscoveredPaths.get(0));
                break;
        }
    }
}
