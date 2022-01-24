package ca.collene.soccer.services;

public class GameDoesNotExistException extends Exception {
    public GameDoesNotExistException(String message) {
        super(message);
    }

    public GameDoesNotExistException(String message, Exception e) {
        super(message, e);
    }
}
