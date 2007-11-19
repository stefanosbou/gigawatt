package com.dojoconsulting.oanda.fxtrade.api;

/**
 * MinMaxPoint class is a container for min/max graph information.  A MinMaxPoint is calculated from a HistoryPoint
 * directly.
 */
public class MinMaxPoint implements Cloneable {
    private long timestamp;
    private double min;
    private double max;

    public MinMaxPoint() {
    }

    public MinMaxPoint(final long aTimestamp, final double aMin, final double aMax) {
        this.timestamp = aTimestamp;
        this.min = aMin;
        this.max = aMax;
    }

    public long getTimestamp() {
        return timestamp;
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
        //todoproper: Implement toString()
    }
}
