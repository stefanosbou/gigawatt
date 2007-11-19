package com.dojoconsulting.oanda.fxtrade.api;

/**
 * An FXGame object facilitates communication with OANDA's FXGame servers.
 */
public final class FXGame extends FXClient {

    protected String getClientType() {
        return "FXGame";
    }
}
