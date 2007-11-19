package com.dojoconsulting.oanda.fxtrade.api;

/**
 * InvalidExpiryException is thrown by setting an invalid entry order duration.
 */
public class InvalidExpiryException extends OrderException {
    public InvalidExpiryException() {
    }

    public InvalidExpiryException(final String message) {
        super(message);
    }
}
