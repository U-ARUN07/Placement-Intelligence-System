package placement.exception;

/**
 * Custom exception for Placement Intelligence System errors.
 */
public class PlacementException extends Exception {

    private final String errorCode;

    public PlacementException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PlacementException(String message) {
        super(message);
        this.errorCode = "GENERAL_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "[" + errorCode + "] " + getMessage();
    }
}
