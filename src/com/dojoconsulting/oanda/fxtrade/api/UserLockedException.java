package com.dojoconsulting.oanda.fxtrade.api;

/**
 * A UserLockedException is thrown if too many login attempts occur.
 */
public class UserLockedException extends OAException {

    public UserLockedException() {
        super();
    }

    public UserLockedException(final String message) {
        super(message);
    }
}
