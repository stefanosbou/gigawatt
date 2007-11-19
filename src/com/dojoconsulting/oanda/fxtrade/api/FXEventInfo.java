package com.dojoconsulting.oanda.fxtrade.api;

/**
 * Interface for data passed to an FXEvent
 */
public abstract class FXEventInfo {
    public abstract int compareTo(Object other);

    public abstract long getTimestamp();

    public FXEventInfo() {
    }
}
