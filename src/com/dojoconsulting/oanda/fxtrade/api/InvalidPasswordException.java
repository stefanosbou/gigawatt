package com.dojoconsulting.oanda.fxtrade.api;

/**
 * An InvalidPasswordException is thrown if the password provided at login is invalid.
 */
public class InvalidPasswordException extends OAException {
    public InvalidPasswordException() {
        super();
    }

    public InvalidPasswordException(final String message) {
        super(message);
    }
}
