package com.dojoconsulting.oanda.fxtrade.api;

/**
 * OAException is the base exception class.  All exceptions thrown by the API will be of this type.
 */
public class OAException extends Exception {
    public OAException() {
        super();
    }

    public OAException(final String message) {
        super(message);
    }
}
