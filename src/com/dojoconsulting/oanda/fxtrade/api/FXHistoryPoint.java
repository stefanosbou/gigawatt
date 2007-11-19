package com.dojoconsulting.oanda.fxtrade.api;

/**
 * A useful container class that holds FXTick objects representing the opening, closing, minimum and maximum bid and
 * ask prices.
 */
public class FXHistoryPoint implements Cloneable {
    private long timestamp;

    private CandlePoint candlePoint;
    private FXTick open;
    private FXTick min;
    private FXTick max;
    private FXTick close;
    private boolean corrected;
    private String stringRepresentation;
    private MinMaxPoint minMaxPoint;

    public FXHistoryPoint() {
        this.timestamp = 0;
        this.candlePoint = new CandlePoint(0, 0, 0, 0, 0);
        this.open = new FXTick(0, 0, 0);
        this.min = new FXTick(0, 0, 0);
        this.max = new FXTick(0, 0, 0);
        this.close = new FXTick(0, 0, 0);
        this.open = new FXTick(0, 0, 0);

        this.minMaxPoint = new MinMaxPoint(0, 0, 0);

        this.corrected = false;
        this.stringRepresentation = "0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0";
    }

    public FXHistoryPoint(final long timestamp, final double openBid, final double openAsk, final double closeBid, final double closeAsk, final double minBid, final double maxBid, final double minAsk, final double maxAsk) {
        this.timestamp = timestamp;

        this.open = new FXTick(timestamp, openBid, openAsk);
        this.min = new FXTick(timestamp, minBid, minAsk);
        this.max = new FXTick(timestamp, maxBid, maxAsk);
        this.close = new FXTick(timestamp, closeBid, closeAsk);
        this.candlePoint = new CandlePoint(timestamp, open.getMean(), close.getMean(), min.getMean(), max.getMean());

        this.minMaxPoint = new MinMaxPoint(timestamp, min.getMean(), max.getMean());

        this.corrected = false;

        final StringBuffer buffer = new StringBuffer();
        buffer.append(timestamp).append(" ");
        buffer.append(maxBid).append(" ");
        buffer.append(maxAsk).append(" ");
        buffer.append(openBid).append(" ");
        buffer.append(openAsk).append(" ");
        buffer.append(closeBid).append(" ");
        buffer.append(closeAsk).append(" ");
        buffer.append(minBid).append(" ");
        buffer.append(minAsk);

        this.stringRepresentation = buffer.toString();
    }

    public CandlePoint getCandlePoint() {
        return candlePoint;
    }

    public FXTick getOpen() {
        return open;
    }

    public FXTick getMin() {
        return min;
    }

    public FXTick getMax() {
        return max;
    }

    public FXTick getClose() {
        return close;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean getCorrected() {
        return corrected;
    }

    public void setCorrected(final boolean corrected) {
        this.corrected = corrected;
    }

    public String toString() {
        return stringRepresentation;
    }

    public MinMaxPoint getMinMaxPoint() {
        return minMaxPoint;
    }

    public Object clone() throws CloneNotSupportedException {
        return new FXHistoryPoint(timestamp, open.getBid(), open.getAsk(), close.getBid(), close.getAsk(), min.getBid(), min.getAsk(), max.getBid(), max.getAsk());
    }
}
