package com.dojoconsulting.oanda.fxtrade.api;

/**
 * AccountException is the base exception thrown by any Account object function.
 */
public class AccountException extends OAException {
    public AccountException() {
    }

    public AccountException(final String message) {
        super(message);
    }
}
