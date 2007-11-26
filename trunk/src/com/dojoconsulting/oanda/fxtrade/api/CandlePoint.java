package com.dojoconsulting.oanda.fxtrade.api;

/**
 * CandlePoint class is a container for candle information.  A candle is calculated from a HistoryPoint directly.
 */

public class CandlePoint implements Cloneable {
    private long timestamp;
    private double open;
    private double close;
    private double min;
    private double max;

    public CandlePoint(final long timestamp, final double open, final double close, final double min, final double max) {
        this.timestamp = timestamp;
        this.open = open;
        this.close = close;
        this.min = min;
        this.max = max;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return super.toString();
        //TODOproper: Implement toString()
    }

}
