package com.dojoconsulting.oanda.fxtrade.data;

import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.gigawatt.core.IMarketManager;
import com.dojoconsulting.gigawatt.core.fximpl.FXMarketManager;
import com.dojoconsulting.gigawatt.data.IMarketData;
import com.dojoconsulting.gigawatt.data.ITick;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 27-Oct-2007
 * Time: 20:25:30
 */
public abstract class GenericFXMarketDataReader implements IMarketData {
	private final FXPair pair;
	private final String filePath;
	private BufferedReader in;
	private FXTick lastTick = new FXTick(0, 0.0, 0.0);
	private FXTick nextPossibleTick = new FXTick(0, 0.0, 0.0);

	private DateFormat formatter;

	private FXMarketManager marketManager;

	public void setMarketManager(final IMarketManager marketManager) {
		this.marketManager = (FXMarketManager) marketManager;
	}

	public GenericFXMarketDataReader(final FXPair pair, final String path) {
		this.pair = pair;
		this.filePath = path;
	}

	public void init() {
		try {
			in = new BufferedReader(new FileReader(filePath));
		}
		catch (FileNotFoundException e) {
			throw new GigawattException("GenericFXMarketDataReader: Could not find the file (" + filePath + ") for " + pair, e);
		}
	}

	public boolean hasMoreTicks() {
		try {
			return in.ready() || nextPossibleTick != null;
		}
		catch (IOException e) {
			throw new GigawattException("GenericFXMarketDataReader: There was a problem reading market data for " + pair, e);
		}
	}

	public ITick getNextTick(final long currentTimeInMillis) {
		final TickRecord tickRecord = new TickRecord();
		String dataRecord = null;
		try {
			if (nextPossibleTick != null) {
				if (nextPossibleTick.getTimestamp() > currentTimeInMillis) {
					return null;
				}
				lastTick = nextPossibleTick;
				marketManager.registerTick(pair, lastTick);
				nextPossibleTick = null;
			}
			if (!in.ready()) {
				return null;
			}
			while (in.ready()) {
				dataRecord = in.readLine();
				if (dataRecord == null) {
					return null;
				}
				processRecord(dataRecord, tickRecord);
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
		catch (IOException e) {
			throw new GigawattException("GenericFXMarketDataReader: There was a problem reading market data for " + pair, e);
		}
		catch (ParseException e) {
			throw new GigawattException("GenericFXMarketDataReader: There was a problem parsing the date in " + pair + " with the following data: '" + dataRecord + "'.", e);
		}
	}

	public FXPair getProduct() {
		return pair;
	}

	protected abstract void processRecord(final String record, final TickRecord tickRecord) throws ParseException;

	protected class TickRecord {
		private long timeInMillis;
		private double bid;
		private double ask;

		public void setTimeInMillis(final long timeInMillis) {
			this.timeInMillis = timeInMillis;
		}

		public void setBid(final double bid) {
			this.bid = bid;
		}

		public void setAsk(final double ask) {
			this.ask = ask;
		}
	}
}
