package com.dojoconsulting.oanda.fxtrade.api;

/**
 * UserException is the base exception thrown by any User object function.
 */
public class UserException extends OAException {

    public UserException() {
        super();
    }

    public UserException(final String message) {
        super(message);
    }
}
