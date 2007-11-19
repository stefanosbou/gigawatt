package com.dojoconsulting.oanda.fxtrade.api;

/**
 * A SessionErrorException is thrown if an unknown error occurs.
 */
public class SessionErrorException extends SessionException {

    public SessionErrorException() {
        super();
    }

    public SessionErrorException(final String message) {
        super(message);
    }
}
