package bg.sofia.uni.fmi.mjt.battleships.exceptions;

public class IllegalShipPlacementException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalShipPlacementException(String message) {
        super(message);
    }

}
