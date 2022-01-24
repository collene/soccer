package ca.collene.soccer.services;

public class PlayerAlreadyOnTeamException extends Exception {
    public PlayerAlreadyOnTeamException(String message) {
        super(message);
    }

    public PlayerAlreadyOnTeamException(String message, Exception e) {
        super(message, e);
    }
}
