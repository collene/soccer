package ca.collene.soccer.services;

public class InvalidScoreException extends Exception {
    public InvalidScoreException(String message) {
        super(message);
    }

    public InvalidScoreException(String message, Exception e) {
        super(message, e);
    }
}
