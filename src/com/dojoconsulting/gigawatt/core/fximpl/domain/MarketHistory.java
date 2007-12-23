package com.dojoconsulting.gigawatt.core.fximpl.domain;

import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.gigawatt.data.ITick;
import com.dojoconsulting.oanda.fxtrade.api.FXClient;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 25-Nov-2007
 * Time: 15:31:45
 */
public class MarketHistory {
	private FXPair pair;

	private MarketHistoryPeriod intervalFiveSecond;
	private MarketHistoryPeriod intervalTenSecond;
	private MarketHistoryPeriod intervalThirtySecond;

	private MarketHistoryPeriod intervalOneMin;
	private MarketHistoryPeriod intervalFiveMinute;
	private MarketHistoryPeriod intervalFifteenMinute;
	private MarketHistoryPeriod intervalThirtyMinute;

	private MarketHistoryPeriod intervalOneHour;
	private MarketHistoryPeriod intervalThreeHour;

	private MarketHistoryPeriod intervalOneDay;

	public MarketHistory(final FXPair pair) {
		this.pair = pair;
		intervalFiveSecond = new MarketHistoryPeriod(pair, FXClient.INTERVAL_5_SEC);
		intervalTenSecond = new MarketHistoryPeriod(pair, FXClient.INTERVAL_10_SEC);
		intervalThirtySecond = new MarketHistoryPeriod(pair, FXClient.INTERVAL_30_SEC);

		intervalOneMin = new MarketHistoryPeriod(pair, FXClient.INTERVAL_1_MIN);
		intervalFiveMinute = new MarketHistoryPeriod(pair, FXClient.INTERVAL_5_MIN);
		intervalFifteenMinute = new MarketHistoryPeriod(pair, FXClient.INTERVAL_15_MIN);
		intervalThirtyMinute = new MarketHistoryPeriod(pair, FXClient.INTERVAL_30_MIN);

		intervalOneHour = new MarketHistoryPeriod(pair, FXClient.INTERVAL_1_HOUR);
		intervalThreeHour = new MarketHistoryPeriod(pair, FXClient.INTERVAL_3_HOUR);
		intervalOneDay = new MarketHistoryPeriod(pair, FXClient.INTERVAL_1_DAY);
	}

	public FXPair getPair() {
		return pair;
	}

	public void registerTick(final ITick tick) {
		if (tick.getTimestamp() != 0) {
			intervalFiveSecond.registerTick((FXTick) tick);
			intervalTenSecond.registerTick((FXTick) tick);
			intervalThirtySecond.registerTick((FXTick) tick);

			intervalOneMin.registerTick((FXTick) tick);
			intervalFiveMinute.registerTick((FXTick) tick);
			intervalFifteenMinute.registerTick((FXTick) tick);
			intervalThirtyMinute.registerTick((FXTick) tick);

			intervalOneHour.registerTick((FXTick) tick);
			intervalThreeHour.registerTick((FXTick) tick);
			intervalOneDay.registerTick((FXTick) tick);
		}
	}

	public List getHistory(final long interval, int numTicks) {
		final List cache;
		if (numTicks > MarketHistoryPeriod.MAX_TICKS) {
			numTicks = MarketHistoryPeriod.MAX_TICKS;
		}
		if (interval == FXClient.INTERVAL_5_SEC) {
			cache = intervalFiveSecond.getCache();
		} else if (interval == FXClient.INTERVAL_10_SEC) {
			cache = intervalTenSecond.getCache();
		} else if (interval == FXClient.INTERVAL_30_SEC) {
			cache = intervalThirtySecond.getCache();
		} else if (interval == FXClient.INTERVAL_1_MIN) {
			cache = intervalOneMin.getCache();
		} else if (interval == FXClient.INTERVAL_5_MIN) {
			cache = intervalFiveMinute.getCache();
		} else if (interval == FXClient.INTERVAL_15_MIN) {
			cache = intervalFifteenMinute.getCache();
		} else if (interval == FXClient.INTERVAL_30_MIN) {
			cache = intervalThirtyMinute.getCache();
		} else if (interval == FXClient.INTERVAL_1_HOUR) {
			cache = intervalOneHour.getCache();
		} else if (interval == FXClient.INTERVAL_3_HOUR) {
			cache = intervalThreeHour.getCache();
		} else if (interval == FXClient.INTERVAL_1_DAY) {
			cache = intervalOneDay.getCache();
			if (numTicks > MarketHistoryPeriod.MAX_TICKS_DAILY) {
				numTicks = MarketHistoryPeriod.MAX_TICKS_DAILY;
			}
		} else {
			throw new GigawattException("Request for history points used an invalid interval of " + interval);
		}

		return cache.subList(0, numTicks);
	}
}
