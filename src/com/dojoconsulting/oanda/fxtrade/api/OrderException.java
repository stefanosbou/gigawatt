package com.dojoconsulting.oanda.fxtrade.api;

public class OrderException extends OAException {
    public OrderException() {
    }

    public OrderException(final String message) {
        super(message);
    }
}
