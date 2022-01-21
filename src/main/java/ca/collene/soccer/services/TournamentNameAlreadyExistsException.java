package ca.collene.soccer.services;

public class TournamentNameAlreadyExistsException extends Exception {
    public TournamentNameAlreadyExistsException(String message) {
        super(message);
    }

    public TournamentNameAlreadyExistsException(String message, Exception e) {
        super(message, e);
    }
}
