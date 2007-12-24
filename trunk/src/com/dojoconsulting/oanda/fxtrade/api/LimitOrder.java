package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.GigawattException;

/**
 * A LimitOrder is a spot order that is executed when the target price is met.  The StopLossOrder and TakeProfitOrder
 * members will be carried over to the resulting trade.
 */
public final class LimitOrder extends EntryOrder implements Cloneable {

	private static final long MAX_EXPIRY = 30 * 24 * 60 * 60 * 1000l;
	private boolean closed = false;

	public LimitOrder(final long expiry) {
		super(expiry);
	}

	public String toString() {
		return super.toString();
		//TODO proper: Implement toString()
	}

	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new GigawattException("CloneNotSupported for LimitOrder", e);
		}
	}

	void validate(final long currentTime) throws OAException {
		final long expiry = getExpiry();
		if (getPair() == null) {
			throw new OAException("You must set a valid pair.");
		}
		if (expiry < currentTime) {
			throw new OAException("Expiry date cannot be before current time");
		}
		if (expiry > (currentTime + MAX_EXPIRY)) {
			throw new OAException("Expiry date cannot be greater than 30 days or 2592000 seconds");
		}
		if (getUnits() == 0) {
			throw new OAException("You cannot execute a LimitOrder with 0 units.");
		}
		if (getPrice() <= 0.0) {
			throw new OAException("Limit price of " + getPrice() + " is invalid.");
		}
		validateOrders();
	}

	void validateOrders() throws OAException {
		final StopLossOrder stopLoss = getStopLoss();
		final TakeProfitOrder takeProfit = getTakeProfit();
		final double price = getPrice();

		if (stopLoss != null) {
			// If this is a sell, the stopLoss needs to be greater than the limit price
			if (isShort()) {
				if (stopLoss.getPrice() <= price) {
					throw new OAException("Stop loss must be above limit ( " + price + ")");
				}
			}
			// If this is a buy, the stopLoss needs to be less than than the limit price
			if (isLong()) {
				if (stopLoss.getPrice() >= price) {
					throw new OAException("Stop loss must be below limit ( " + price + ")");
				}
			}
		}
		if (takeProfit != null) {
			// If this is a sell, the takeProfit needs to be less than the limit price
			if (isShort()) {
				if (takeProfit.getPrice() >= price) {
					throw new OAException("Take profit must be below limit ( " + price + ")");
				}
			}
			// If this is a buy, the stopLoss needs to be greater than the limit price
			if (isLong()) {
				if (takeProfit.getPrice() <= price) {
					throw new OAException("Take profit must be above limit ( " + price + ")");
				}
			}
		}
	}

	void setClosed(final boolean closed) {
		this.closed = closed;
	}

	boolean isClosed() {
		return closed;
	}
}
