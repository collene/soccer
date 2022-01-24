package ca.collene.soccer.services;

public class TeamNotInGameException extends Exception {
    public TeamNotInGameException(String message) {
        super(message);
    }

    public TeamNotInGameException(String message, Exception e) {
        super(message, e);
    }
}
