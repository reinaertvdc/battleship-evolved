package be.uhasselt.ttui.battleshipevolved;

/**
 * Created by Arno on 11/11/2015.
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
