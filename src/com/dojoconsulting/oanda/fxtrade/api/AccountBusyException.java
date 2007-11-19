package com.dojoconsulting.oanda.fxtrade.api;

/**
 * AccountBusyException is thrown if a command cannot be executed because the account is busy.
 */
public class AccountBusyException extends AccountException {
    public AccountBusyException() {
        super();
    }

    public AccountBusyException(final String message) {
        super(message);
    }
}
