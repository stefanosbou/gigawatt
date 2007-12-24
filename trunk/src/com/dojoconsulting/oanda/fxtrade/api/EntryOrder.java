package com.dojoconsulting.oanda.fxtrade.api;

/**
 * EntryOrder is an abstract base class extending classOrder to include expiry and desired execution price information.
 */
public abstract class EntryOrder extends Order {

	private long expiry;

	public EntryOrder(final long expiry) {
		this.expiry = expiry;
	}

	public long getExpiry() {
		return expiry;
	}

	public void setExpiry(final long expiry) {
		this.expiry = expiry;
	}

	public void setPrice(final double price) {
		super.setPrice(price);
	}
}
