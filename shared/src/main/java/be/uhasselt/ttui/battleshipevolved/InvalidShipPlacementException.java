package be.uhasselt.ttui.battleshipevolved;

/**
 * @author Arno Stienaers
 */
public class InvalidShipPlacementException extends Exception {
    private String mError;

    public InvalidShipPlacementException(){
        mError = "Ship placed is invalid.";
    }

    public InvalidShipPlacementException(String error){
        mError = error;
    }

    public String getError() {
        return mError;
    }
}
