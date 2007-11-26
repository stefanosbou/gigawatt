package com.dojoconsulting.oanda.fxtrade.api;

import java.util.ArrayList;
import java.util.List;

/**
 * A Position represents an aggregation of several open market orders
 */
public class Position {
	private FXPair pair;
	private List<MarketOrder> trades;

	Position(final FXPair pair) {
		trades = new ArrayList<MarketOrder>();
		this.pair = pair;
	}

	public double getUnrealizedPL(final FXTick currPrice) {
		double result = 0;
		final int size = trades.size();
		for (int i = 0; i < size; i++) {
			final MarketOrder trade = trades.get(i);
			result += trade.getUnrealizedPL(currPrice);
		}
		return result;
	}

	public String toString() {
		//todoproper: Implement toString()
		return super.toString();
	}

	public FXPair getPair() {
		return pair;
	}

	public double getPrice() {
		double pricedUnits = 0;
		double totalUnits = 0;
		for (final MarketOrder trade : trades) {
			pricedUnits += (trade.getUnits() * trade.getPrice());
			totalUnits += trade.getUnits();
		}
		return pricedUnits / totalUnits;
	}

	public long getUnits() {
		long result = 0;
		for (final MarketOrder trade : trades) {
			result += trade.getUnits();
		}
		return result;
	}

	void close() {
		trades.clear();
	}

	void closeTrade(final MarketOrder trade) {
		trades.remove(trade);
	}

	void addMarketOrder(final MarketOrder trade) {
		if (trade.getPair().equals(this.pair)) {
			trades.add(trade);
		}
	}

	List<MarketOrder> getTrades() {
		return trades;
	}

}
