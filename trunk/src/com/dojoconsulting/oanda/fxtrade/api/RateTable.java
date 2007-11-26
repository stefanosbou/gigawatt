package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.IHistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The RateTable object holds all incoming rate information.  A ratetable is used to obtain rate info (quotes for
 * currency pairs) and history data for these as well.
 */
public final class RateTable {
	private Map tickTable;
	private IHistoryManager historyManager;

	private FXEventManager eventManager = new FXEventManager() {
		@SuppressWarnings("unchecked")
		final void event(final FXRateEventInfo ei) {
			final List<FXEvent> events = (List<FXEvent>) getEvents();
			for (final FXEvent event : events) {
				if (event instanceof FXRateEvent) {
					final FXRateEvent rateEvent = (FXRateEvent) event;
					if (rateEvent.match(ei)) {
						event.handle(ei, this);
					}
				}
			}
		}
	};


	public RateTable() {
		this.tickTable = new HashMap();
	}

	public FXTick getRate(final FXPair pair) throws RateTableException {
		return (FXTick) tickTable.get(pair);
	}

	void setTickTable(final Map tickTable) {
		this.tickTable = tickTable;
	}

	public FXEventManager getEventManager() {
		return eventManager;
	}

	@SuppressWarnings("unchecked")
	public List getCandles(final FXPair pair, final long interval, final int numTicks) throws OAException {
		//TODO: Decide whether this should be cached and pushed down to MarketHistory layer
		final List<FXHistoryPoint> history = (List<FXHistoryPoint>) getHistory(pair, interval, numTicks);
		final List candles = new ArrayList(history.size());
		for (final FXHistoryPoint aHistory : history) {
			candles.add(aHistory.getCandlePoint());
		}
		return candles;
	}

	public List getHistory(final FXPair pair, final long interval, final int numTicks) throws OAException {
		return historyManager.getHistory(pair, interval, numTicks);
	}

	@SuppressWarnings("unchecked")
	public List getMinMaxs(final FXPair pair, final long interval, final int numTicks) throws OAException {
		//TODO: Decide whether this should be cached and pushed down to MarketHistory layer
		final List<FXHistoryPoint> history = (List<FXHistoryPoint>) getHistory(pair, interval, numTicks);
		final List minMaxs = new ArrayList(history.size());
		for (final FXHistoryPoint aHistory : history) {
			minMaxs.add(aHistory.getMinMaxPoint());
		}
		return minMaxs;
	}

	public boolean loggedIn() {
		return true;
	}

	void setHistoryManager(final IHistoryManager historyManager) {
		this.historyManager = historyManager;
	}
}
