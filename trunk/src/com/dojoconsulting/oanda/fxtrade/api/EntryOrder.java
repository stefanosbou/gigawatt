package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.TimeServer;


/**
 * EntryOrder is an abstract base class extending classOrder to include expiry and desired execution price information.
 */
public abstract class EntryOrder extends Order {

	private long expiry;
	private static final long THIRTY_DAYS_IN_MILLIS = 2592000;

	public EntryOrder(final long expiry) {
		this.expiry = expiry;
	}

	public long getExpiry() {
		return expiry;
	}

	public void setExpiry(final long expiry) {
		final long currentTime = TimeServer.getInstance().getTime();
		if (expiry > currentTime && expiry < (currentTime + THIRTY_DAYS_IN_MILLIS)) {
			this.expiry = expiry;
		}
	}

	public void setPrice(final double price) {
		super.setPrice(price);
	}
}
