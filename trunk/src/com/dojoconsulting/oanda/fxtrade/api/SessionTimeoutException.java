package com.dojoconsulting.oanda.fxtrade.api;

/**
 * A SessionTimeoutException is thrown if server communication exceeds the timeout threshold.
 */
public class SessionTimeoutException extends SessionException {

    public SessionTimeoutException() {
        super();
    }

    public SessionTimeoutException(final String message) {
        super(message);
    }
}
