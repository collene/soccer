package ca.collene.soccer.services;

public class GameAlreadyInTournamentException extends Exception {
    public GameAlreadyInTournamentException(String message) {
        super(message);
    }

    public GameAlreadyInTournamentException(String message, Exception e) {
        super(message, e);
    }
}
