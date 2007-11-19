package com.dojoconsulting.oanda.fxtrade.api;

/**
 * An InvalidUserException is thrown if the username provided at login does not exist.
 */
public class InvalidUserException extends OAException {
    public InvalidUserException() {
        super();
    }

    public InvalidUserException(final String message) {
        super(message);
    }
}
