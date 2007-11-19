package com.dojoconsulting.oanda.fxtrade.api;

/**
 * InvalidPriceException is thrown by setting an invalid execution price
 */
public class InvalidPriceException extends OrderException {
    public InvalidPriceException() {
    }

    public InvalidPriceException(final String message) {
        super(message);
    }
}
