package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.GigawattException;

/**
 * A MarketOrder is used to create a spot trade.
 */
public final class MarketOrder extends Order implements Cloneable {

	private int transactionLink;
	private boolean closed;
	private MarketOrder closePrice;

	public MarketOrder() {
	}

	public MarketOrder getClose() {
		return closePrice;
		//TODO: getClose() + setClose() - Is this the correct implementation?
	}

	public double getUnrealizedPL(final FXTick tick) {
		final double currentRate;
		if (isLong()) {
			currentRate = tick.getBid();
		} else {
			currentRate = tick.getAsk();
		}
		final double profit = (currentRate - getPrice()) * getUnits();
		return UtilMath.round(profit, 5);
		//TODO:  What does this method return if the trade is closed?
	}

	public int getTransactionLink() {
		return transactionLink;
	}

	public double getRealizedPL() {
		if (!closed) {
			return 0;
		}
		final double profit = (closePrice.getPrice() - getPrice()) * getUnits();
		return UtilMath.round(profit, 5);
	}

	public String toString() {
		return super.toString();
		//TODO proper: Implement toString()
	}


	public Object clone() {
		final MarketOrder order;
		try {
			order = (MarketOrder) super.clone();
			order.setHighPriceLimit(getHighPriceLimit());
			order.setLowPriceLimit(getLowPriceLimit());
			order.setPair((FXPair) getPair().clone());
			order.setPrice(getPrice());
			if (getStopLoss() != null) {
				order.setStopLoss((StopLossOrder) getStopLoss().clone());
			}
			if (getTakeProfit() != null) {
				order.setTakeProfit((TakeProfitOrder) getTakeProfit().clone());

			}
			order.setTimestamp(getTimestamp());
			order.setTransactionNumber(getTransactionNumber());
			order.setUnits(getUnits());
			return order;
		}
		catch (CloneNotSupportedException e) {
			throw new GigawattException("Problem cloning MarketOrder");
		}
	}

	void validate(final FXTick tick) throws OAException {
		if (getPair() == null) {
			throw new OAException("You must set a valid pair.");
		}
		if (isShort()) {
			setPrice(tick.getBid());
		} else if (isLong()) {
			setPrice(tick.getAsk());
		} else {
			throw new OAException("You cannot execute a MarketOrder with 0 units.");
		}
		validateOrders();
	}

	void validateOrders() throws OAException {
		final StopLossOrder stopLoss = getStopLoss();
		final TakeProfitOrder takeProfit = getTakeProfit();
		final double price = getPrice();

		if (stopLoss != null) {
			// If this is a sell, the stopLoss needs to be greater than the execution price
			if (isShort()) {
				if (stopLoss.getPrice() <= price) {
					throw new OAException("Stop loss must be above quote ( " + price + ")");
				}
			}
			// If this is a buy, the stopLoss needs to be less than than the execution price
			if (isLong()) {
				if (stopLoss.getPrice() >= price) {
					throw new OAException("Stop loss must be below quote ( " + price + ")");
				}
			}
		}
		if (takeProfit != null) {
			// If this is a sell, the takeProfit needs to be less than the execution price
			if (isShort()) {
				if (takeProfit.getPrice() >= price) {
					throw new OAException("Take profit must be below quote ( " + price + ")");
				}
			}
			// If this is a buy, the stopLoss needs to be greater than the execution price
			if (isLong()) {
				if (takeProfit.getPrice() <= price) {
					throw new OAException("Take profit must be above quote ( " + price + ")");
				}
			}
		}
	}

	void setClose(final MarketOrder closePrice) {
		this.closePrice = closePrice;
		closed = true;
	}

	void setTransactionLink(final int transactionLink) {
		this.transactionLink = transactionLink;
	}

	boolean isClosed() {
		return (getClose() != null);
	}

	public boolean isBuy() {
		return getUnits() > 0;
	}
}
