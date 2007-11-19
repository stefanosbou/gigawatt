package com.dojoconsulting.oanda.fxtrade.api;

/**
 * An FXTrade object facilitates communication with OANDA's FXTrade servers.
 */
public final class FXTrade extends FXClient {
    protected String getClientType() {
        return "FXTrade";
    }
}
