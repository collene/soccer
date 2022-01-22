package ca.collene.soccer.services;

public class TeamAlreadyInTournamentException extends Exception {
    public TeamAlreadyInTournamentException(String message) {
        super(message);
    }

    public TeamAlreadyInTournamentException(String message, Exception e) {
        super(message, e);
    }
}
