package com.dojoconsulting.oanda.fxtrade.api;

/**
 * An FXRateEventInfo object encapsulates a new market rate received from the server.
 */
public final class FXRateEventInfo extends FXEventInfo {
    private FXPair pair;
    private FXTick tick;
    private long timestamp;

    FXRateEventInfo(final FXPair pair, final FXTick tick, final long timestamp) {
        this.pair = pair;
        this.tick = tick;
        this.timestamp = timestamp;
    }

    public FXPair getPair() {
        return pair;
    }

    public FXTick getTick() {
        return tick;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean equals(final Object other) {
        //TODO proper: Implement proper equals()
        return other instanceof FXRateEventInfo && super.equals(other);
    }

    public int compareTo(final Object other) {
        return 0;
        //TODO proper: Implement proper compareTo()
    }

}
