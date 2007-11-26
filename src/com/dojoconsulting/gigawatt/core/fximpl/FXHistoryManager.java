package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.config.MarketConfig;
import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.gigawatt.core.IHistoryManager;
import com.dojoconsulting.gigawatt.data.ITick;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 25-Nov-2007
 * Time: 15:25:18
 */
public class FXHistoryManager implements IHistoryManager {

	private Map<FXPair, MarketHistory> marketHistories;

	public void init(final BackTestConfig config) {
		marketHistories = new HashMap<FXPair, MarketHistory>();

		final List<MarketConfig> marketConfigs = config.getMarkets();
		for (final MarketConfig marketConfig : marketConfigs) {
			if (marketConfig.requiresKeepHistory()) {
				final FXPair pair = new FXPair(marketConfig.getProduct());
				final MarketHistory marketHistory = new MarketHistory(pair);
				marketHistories.put(pair, marketHistory);
			}
		}
	}

	public void registerTick(final FXPair pair, final ITick tick) {
		if (marketHistories.containsKey(pair)) {
			final MarketHistory history = marketHistories.get(pair);
			history.registerTick(tick);
		}
	}

	public List getHistory(final FXPair pair, final long interval, final int numTicks) {
		if (!marketHistories.containsKey(pair)) {
			throw new GigawattException("Market Configuration did not request history caching for pair " + pair);
		}
		final MarketHistory history = marketHistories.get(pair);
		return history.getHistory(interval, numTicks);
	}
}
