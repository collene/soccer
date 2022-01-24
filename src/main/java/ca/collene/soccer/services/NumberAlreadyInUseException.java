package ca.collene.soccer.services;

public class NumberAlreadyInUseException extends Exception {
    public NumberAlreadyInUseException(String message) {
        super(message);
    }

    public NumberAlreadyInUseException(String message, Exception e) {
        super(message, e);
    }
}
