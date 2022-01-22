package ca.collene.soccer.services;

public class CoachAlreadyOnTeamException extends Exception {
    public CoachAlreadyOnTeamException(String message) {
        super(message);
    }

    public CoachAlreadyOnTeamException(String message, Exception e) {
        super(message, e);
    }
}
