package ca.collene.soccer.services;

public class TeamDoesNotExistException extends Exception {
    public TeamDoesNotExistException(String message) {
        super(message);
    }

    public TeamDoesNotExistException(String message, Exception e) {
        super(message, e);
    }
}
