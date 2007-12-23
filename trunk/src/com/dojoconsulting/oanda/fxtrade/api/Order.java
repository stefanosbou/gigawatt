package com.dojoconsulting.oanda.fxtrade.api;

/**
 * Note that a sell order is indicated by specifying negative units.
 */
public abstract class Order {
	private double highPriceLimit;
	private double lowPriceLimit;
	private FXPair pair;
	private StopLossOrder stopLoss;
	private TakeProfitOrder takeProfit;
	private long units;

	private long timestamp;
	private int transactionNumber;

	private double price;

	public Order() {
	}

	void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	void setTransactionNumber(final int transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	void setPrice(final double price) {
		this.price = price;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getTransactionNumber() {
		return transactionNumber;
	}

	public double getPrice() {
		return price;
	}

	public double getHighPriceLimit() {
		return highPriceLimit;
	}

	public void setHighPriceLimit(final double highPriceLimit) {
		this.highPriceLimit = highPriceLimit;
	}

	public double getLowPriceLimit() {
		return lowPriceLimit;
	}

	public void setLowPriceLimit(final double lowPriceLimit) {
		this.lowPriceLimit = lowPriceLimit;
	}

	public FXPair getPair() {
		return pair;
	}

	public void setPair(final FXPair pair) {
		this.pair = pair;
	}

	public StopLossOrder getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(final StopLossOrder stopLoss) {
		this.stopLoss = stopLoss;
	}

	public TakeProfitOrder getTakeProfit() {
		return takeProfit;
	}

	public void setTakeProfit(final TakeProfitOrder takeProfit) {
		this.takeProfit = takeProfit;
	}

	public long getUnits() {
		return units;
	}

	public void setUnits(final long units) {
		this.units = units;
	}

	boolean isLong() {
		return getUnits() > 0;
	}

	boolean isShort() {
		return getUnits() < 0;
	}
}
