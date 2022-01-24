package ca.collene.soccer.services;

public class GameNotScoredException extends Exception {
    public GameNotScoredException(String message) {
        super(message);
    }

    public GameNotScoredException(String message, Exception e) {
        super(message, e);
    }
}
