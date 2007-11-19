package com.dojoconsulting.oanda.fxtrade.api;

/**
 * NSFException is thrown if an insufficient balance exists to complete an account function
 */
public class NSFException extends AccountException {

    public NSFException() {
        super();
    }

    public NSFException(final String message) {
        super(message);
    }
}
