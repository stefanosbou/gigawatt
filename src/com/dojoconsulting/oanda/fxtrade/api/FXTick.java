package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.data.ITick;

import java.text.DateFormat;
import java.util.Date;

/**
 * An FXTick object represents a single forex spot price.
 */
public final class FXTick implements Cloneable, ITick {
	private long timestamp;
	private double bid;
	private double ask;

	public FXTick() {
		this.timestamp = 0;
		this.bid = 0;
		this.ask = 0;
	}

	public FXTick(final long timestamp, final double bid, final double ask) {
		this.timestamp = timestamp;
		this.bid = bid;
		this.ask = ask;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public double getBid() {
		return bid;
	}

	public double getAsk() {
		return ask;
	}

	public FXTick getInverse() {
		return new FXTick(timestamp, 1 / ask, 1 / bid);
	}

	public double getMean() {
		return (bid + ask) / 2;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public void setBid(final double bid) {
		this.bid = bid;
	}

	public void setAsk(final double ask) {
		this.ask = ask;
	}

	public boolean equals(final Object o) {
		if (!(o instanceof FXTick)) {
			return false;
		}
		final FXTick other = (FXTick) o;
		return (this.timestamp == other.timestamp) && (this.bid == other.bid) && (this.ask == other.ask);
	}

	public Object clone() {
		return new FXTick(timestamp, bid, ask);
	}

	public String toString() {
		final Date date = new Date(timestamp);
		final String strDate = DateFormat.getDateInstance().format(date);
		return strDate + ": " + timestamp + " " + bid + " " + ask;
		//TODO proper: Implement toString()
	}
}
