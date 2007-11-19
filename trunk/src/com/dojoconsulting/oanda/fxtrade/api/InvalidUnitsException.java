package com.dojoconsulting.oanda.fxtrade.api;

public class InvalidUnitsException extends OrderException {

    public InvalidUnitsException() {
        super();
    }

    public InvalidUnitsException(final String message) {
        super(message);
    }
}
