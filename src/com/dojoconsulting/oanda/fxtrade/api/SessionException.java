package com.dojoconsulting.oanda.fxtrade.api;

/**
 * A SessionException is thrown by any object in a session context.
 */
public class SessionException extends OAException {
    public SessionException() {
        super();
    }

    public SessionException(final String message) {
        super(message);
    }
}
