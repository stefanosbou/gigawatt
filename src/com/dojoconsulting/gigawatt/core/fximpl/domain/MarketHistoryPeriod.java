package com.dojoconsulting.gigawatt.core.fximpl.domain;

import com.dojoconsulting.oanda.fxtrade.api.FXClient;
import com.dojoconsulting.oanda.fxtrade.api.FXHistoryPoint;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;
import com.dojoconsulting.oanda.fxtrade.api.UtilAPI;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 25-Nov-2007
 * Time: 20:01:11
 */
public class MarketHistoryPeriod {
	private long interval;
	private FXPair pair;
	private CircularFifoBuffer cache;
	private FXHistoryPoint currentPoint;
	public static final int MAX_TICKS = 500;
	public static final int MAX_TICKS_DAILY = 365;

	MarketHistoryPeriod(final FXPair pair, final long interval) {
		this.pair = pair;
		this.interval = interval;
		int max = MAX_TICKS;
		if (interval == FXClient.INTERVAL_1_DAY) {
			max = MAX_TICKS_DAILY;
		}
		cache = new CircularFifoBuffer(max);
	}

	public FXPair getPair() {
		return pair;
	}

	@SuppressWarnings("unchecked")
	public List getCache() {
		return new ArrayList(cache);
	}

	void registerTick(final FXTick tick) {
		if (currentPoint == null) {
			long firstTimestamp = tick.getTimestamp();
			firstTimestamp = firstTimestamp - (firstTimestamp % interval);
			currentPoint = UtilAPI.createStartingFXHistoryPoint(firstTimestamp, tick);
			cache.add(currentPoint);
			return;
		}
		if (currentPoint.getTimestamp() + interval <= tick.getTimestamp()) {
			currentPoint = getNewCurrent(currentPoint, tick.getTimestamp());
		}
		UtilAPI.updateFXHistoryPoint(currentPoint, tick);
	}

	private FXHistoryPoint getNewCurrent(final FXHistoryPoint currentPoint, final long currentTickTimestamp) {
		FXHistoryPoint latest = currentPoint;
		while (true) {
			final FXHistoryPoint newPoint = UtilAPI.createNewHistoryPointFromOld(latest, interval);
			cache.add(newPoint);
			if (newPoint.getTimestamp() + interval > currentTickTimestamp) {
				return newPoint;
			}
			latest = newPoint;
		}
	}

}
