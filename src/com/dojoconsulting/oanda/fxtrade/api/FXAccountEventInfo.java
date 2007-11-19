package com.dojoconsulting.oanda.fxtrade.api;

/**
 * To change this template use File | Settings | File Templates.
 */
public final class FXAccountEventInfo extends FXEventInfo {
    private Transaction transaction;
    private long timestamp;

    FXAccountEventInfo(final Transaction transaction, final long timestamp) {
        this.transaction = transaction;
        this.timestamp = timestamp;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean equals(final Object other) {
        //todoproper: Implement proper equals()
        return other instanceof FXAccountEventInfo && super.equals(other);
    }

    public int compareTo(final Object other) {
        return 0;
        //todoproper: Implement proper compareTo()
    }

}
