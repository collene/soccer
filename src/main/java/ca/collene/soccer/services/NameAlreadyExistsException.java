package ca.collene.soccer.services;

public class NameAlreadyExistsException extends Exception {
    public NameAlreadyExistsException(String message) {
        super(message);
    }

    public NameAlreadyExistsException(String message, Exception e) {
        super(message, e);
    }
}
