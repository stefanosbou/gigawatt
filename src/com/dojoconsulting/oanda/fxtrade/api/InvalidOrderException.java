package com.dojoconsulting.oanda.fxtrade.api;

/**
 * InvalidOrderException is thrown in a non-existant order is modified or closed
 */
public class InvalidOrderException extends AccountException {

    public InvalidOrderException() {
        super();
    }

    public InvalidOrderException(final String message) {
        super(message);
    }
}
