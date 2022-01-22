package ca.collene.soccer.services;

public class PersonDoesNotExistException extends Exception {
    public PersonDoesNotExistException(String message) {
        super(message);
    }

    public PersonDoesNotExistException(String message, Exception e) {
        super(message, e);
    }
}
