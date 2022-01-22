package ca.collene.soccer.services;

public class TournamentDoesNotExistException extends Exception {
    public TournamentDoesNotExistException(String message) {
        super(message);
    }

    public TournamentDoesNotExistException(String message, Exception e) {
        super(message, e);
    }
}
