package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.GigawattException;

/**
 * A LimitOrder is a spot order that is executed when the target price is met.  The StopLossOrder and TakeProfitOrder
 * members will be carried over to the resulting trade.
 */
public final class LimitOrder extends EntryOrder implements Cloneable {

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
}
