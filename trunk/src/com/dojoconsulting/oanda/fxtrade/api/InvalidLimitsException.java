package com.dojoconsulting.oanda.fxtrade.api;

public class InvalidLimitsException extends OrderException {

    public InvalidLimitsException() {
        super();
    }

    public InvalidLimitsException(final String message) {
        super(message);
    }
}
