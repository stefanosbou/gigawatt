package com.dojoconsulting.oanda.fxtrade.api;

/**
 * A useful container class that holds FXTick objects representing the opening, closing, minimum and maximum bid and
 * ask prices.
 */
public class FXHistoryPoint implements Cloneable {
	private long timestamp;

	private FXTick open;
	private FXTick min;
	private FXTick max;
	private FXTick close;
	private boolean corrected;

	FXHistoryPoint(final long start, final FXTick tick) {
		this.timestamp = start;
		this.open = tick;
		this.min = tick;
		this.max = tick;
		this.close = tick;
	}

	FXHistoryPoint createNextPoint(final long interval) {
		final FXHistoryPoint newPoint = new FXHistoryPoint();
		newPoint.open = this.close;
		newPoint.min = this.close;
		newPoint.max = this.close;
		newPoint.close = this.close;
		newPoint.timestamp = this.timestamp + interval;
		return newPoint;
	}

	void updatePoint(final FXTick newTick) {
		if (min.getMean() > newTick.getMean()) {
			min = newTick;
		} else if (max.getMean() < newTick.getMean()) {
			max = newTick;
		}
		close = newTick;
	}


	public FXHistoryPoint() {
		this.timestamp = 0;
		this.open = new FXTick(0, 0, 0);
		this.min = new FXTick(0, 0, 0);
		this.max = new FXTick(0, 0, 0);
		this.close = new FXTick(0, 0, 0);

		this.corrected = false;
	}

	public FXHistoryPoint(final long timestamp, final double openBid, final double openAsk, final double closeBid, final double closeAsk, final double minBid, final double maxBid, final double minAsk, final double maxAsk) {
		this.timestamp = timestamp;

		this.open = new FXTick(timestamp, openBid, openAsk);
		this.min = new FXTick(timestamp, minBid, minAsk);
		this.max = new FXTick(timestamp, maxBid, maxAsk);
		this.close = new FXTick(timestamp, closeBid, closeAsk);

		this.corrected = false;
	}

	public CandlePoint getCandlePoint() {
		return new CandlePoint(timestamp, open.getMean(), close.getMean(), min.getMean(), max.getMean());
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
		return getTimestamp() + " " +
				max.getBid() + " " +
				max.getAsk() + " " +
				open.getBid() + " " +
				open.getAsk() + " " +
				close.getBid() + " " +
				close.getAsk() + " " +
				min.getBid() + " " +
				min.getAsk();
	}

	public MinMaxPoint getMinMaxPoint() {
		return new MinMaxPoint(timestamp, min.getMean(), max.getMean());
	}

	public Object clone() {
		final FXHistoryPoint clone = new FXHistoryPoint();
		clone.timestamp = this.timestamp;
		clone.open = (FXTick) this.open.clone();
		clone.close = (FXTick) this.close.clone();
		clone.min = (FXTick) this.min.clone();
		clone.max = (FXTick) this.max.clone();
		return clone;
	}
}
