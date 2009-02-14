package com.dojoconsulting.oanda.fxtrade.data;

import com.dojoconsulting.gigawatt.core.IMarketManager;
import com.dojoconsulting.gigawatt.core.fximpl.FXMarketManager;
import com.dojoconsulting.gigawatt.data.IMarketData;
import com.dojoconsulting.gigawatt.data.ITick;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 27-Oct-2007
 * Time: 20:25:30
 */
public abstract class GenericFXMarketDataReader implements IMarketData {
	protected final FXPair pair;
	protected final String filePath;
	private FXTick lastTick = new FXTick(0, 0.0, 0.0);
	private FXTick nextPossibleTick = new FXTick(0, 0.0, 0.0);

	private FXMarketManager marketManager;

	public void setMarketManager(final IMarketManager marketManager) {
		this.marketManager = (FXMarketManager) marketManager;
	}

	public GenericFXMarketDataReader(final FXPair pair, final String path) {
		this.pair = pair;
		this.filePath = path;
	}

	public boolean hasMoreTicks() {
		return nextPossibleTick != null || hasMoreData();
	}

	protected abstract boolean hasMoreData();

	final TickRecord tickRecord = new TickRecord();

	public ITick getNextTick(final long currentTimeInMillis) {
		if (nextPossibleTick != null) {
			if (nextPossibleTick.getTimestamp() > currentTimeInMillis) {
				return null;
			}
			lastTick = nextPossibleTick;
			marketManager.registerTick(pair, lastTick);
			nextPossibleTick = null;
		}
		while (hasMoreData()) {
			getNextRecord(tickRecord);
			if (tickRecord.isEmpty()) {
				return null;
			}
			final long millis = tickRecord.timeInMillis;
			final double bid = tickRecord.bid;
			final double ask = tickRecord.ask;
			if (millis > currentTimeInMillis) {
				nextPossibleTick = new FXTick(millis, bid, ask);
				return lastTick;
			}
			lastTick = new FXTick(millis, bid, ask);
			marketManager.registerTick(pair, lastTick);
		}
		return lastTick;
	}

	public FXPair getProduct() {
		return pair;
	}

	protected abstract void getNextRecord(final TickRecord tickRecord);

	protected class TickRecord {
		private long timeInMillis;
		private double bid;
		private double ask;
		private boolean empty = false;

		public void setTimeInMillis(final long timeInMillis) {
			this.timeInMillis = timeInMillis;
		}

		public void setBid(final double bid) {
			this.bid = bid;
		}

		public void setAsk(final double ask) {
			this.ask = ask;
		}

		public boolean isEmpty() {
			return empty;
		}

		public void setEmpty(final boolean empty) {
			this.empty = empty;
		}

		public long getTimeInMillis() {
			return timeInMillis;
		}

		public double getBid() {
			return bid;
		}

		public double getAsk() {
			return ask;
		}
	}
}
