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
        mAudioHitPaths.add(RESOURCE_PATH + "Missile2.wav");
        mAudioHitPaths.add(RESOURCE_PATH + "Missile3.wav");
        mAudioWaterPaths = new ArrayList<String>();
        mAudioWaterPaths.add(RESOURCE_PATH + "Water1.wav");
        mAudioWaterPaths.add(RESOURCE_PATH + "Water2.wav");
        mAudioWaterPaths.add(RESOURCE_PATH + "Water3.wav");
        mAudioWaterPaths.add(RESOURCE_PATH + "Water4.wav");
        mAudioDiscoveredPaths = new ArrayList<String>();
        mAudioDiscoveredPaths.add(RESOURCE_PATH + "Hint.wav");
        mAudioShipSunkPaths = new ArrayList<String>();
        mAudioShipSunkPaths.add(RESOURCE_PATH + "BattleShipDeath1.wav");
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof SoundType) {
            SoundType sType = (SoundType) arg;
            playSound(sType.getType());
        }
    }

    private void playSound(SoundType.Type type) {
        AudioPlayer player = new AudioPlayer();
        switch (type) {
            case HIT:
                player.play(mAudioHitPaths.get((int)(Math.random() * mAudioHitPaths.size())));
                break;
            case MISSED:
                player.play(mAudioWaterPaths.get((int)(Math.random() * mAudioWaterPaths.size())));
                break;
            case SPOTTED:
                player.play(mAudioDiscoveredPaths.get((int)(Math.random() * mAudioDiscoveredPaths.size())));
                break;
            case SUNK:
                player.play(mAudioShipSunkPaths.get((int)(Math.random() * mAudioShipSunkPaths.size())));
                break;
        }
    }
}
