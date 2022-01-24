package ca.collene.soccer.services;

public class InvalidGameException extends Exception {
    public InvalidGameException(String message) {
        super(message);
    }

    public InvalidGameException(String message, Exception e) {
        super(message, e);
    }
}
