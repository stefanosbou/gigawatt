package com.dojoconsulting.oanda.fxtrade.data;

import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 29-Nov-2007
 * Time: 00:58:11
 */
public abstract class AbstractFileMarketDataReader extends GenericFXMarketDataReader {
	public AbstractFileMarketDataReader(final FXPair pair, final String path) {
		super(pair, path);
	}

	protected boolean hasMoreData() {
		try {
			return in.ready();
		}
		catch (IOException e) {
			throw new GigawattException("GenericFXMarketDataReader: There was a problem reading market data for " + pair, e);
		}

	}

	protected void getNextRecord(final TickRecord tickRecord) {
		String dataRecord = null;
		try {
			if (in.ready()) {
				dataRecord = in.readLine();
				if (dataRecord == null) {
					tickRecord.setEmpty(true);
					return;
				}
				processRecord(dataRecord, tickRecord);
			}
		}
		catch (IOException e) {
			throw new GigawattException("GenericFXMarketDataReader: There was a problem reading market data for " + pair, e);
		}
		catch (ParseException e) {
			throw new GigawattException("GenericFXMarketDataReader: There was a problem parsing the date in " + pair + " with the following data: '" + dataRecord + "'.", e);
		}
	}

	private BufferedReader in;

	public void init() {
		try {
			in = new BufferedReader(new FileReader(filePath));
		}
		catch (FileNotFoundException e) {
			throw new GigawattException("GenericFXMarketDataReader: Could not find the file (" + filePath + ") for " + pair, e);
		}
	}

	protected abstract void processRecord(final String record, final TickRecord tickRecord) throws ParseException;


}
