package be.uhasselt.ttui.battleshipevolved;

/**
 * Created by Arno on 17/12/2015.
 */
public class SoundType {
    public enum Type {HIT, MISSED, SPOTTED, SUNK};
    private Type mType;

    public SoundType (Type type) {
        mType = type;
    }

    public Type getType() {
        return mType;
    }
}
