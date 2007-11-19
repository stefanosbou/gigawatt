package com.dojoconsulting.oanda.fxtrade.api;

/**
 * A RateHistoryModelException is thrown when rate information is not available from the server
 */
public class RateHistoryModelException extends RuntimeException {

    public RateHistoryModelException() {
        super();
    }

    public RateHistoryModelException(final String message) {
        super(message);
    }
}
