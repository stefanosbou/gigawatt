package com.dojoconsulting.oanda.fxtrade.api;

/**
 * A SessionDisconnectedException is thrown if the tcp/ssl connection is interrupted.
 */
public class SessionDisconnectedException extends SessionException {
    public SessionDisconnectedException() {
        super();
    }

    public SessionDisconnectedException(final String message) {
        super(message);
    }
}
