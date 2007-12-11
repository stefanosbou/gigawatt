package com.dojoconsulting.oanda.fxtrade.data;

import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 29-Nov-2007
 * Time: 00:58:11
 */
public class GigawattStreamMarketData extends GenericFXMarketDataReader {
	private boolean moreData = true;

	public GigawattStreamMarketData(final FXPair pair, final String path) {
		super(pair, path);
	}

	protected boolean hasMoreData() {
		return moreData;
	}

	protected void getNextRecord(final TickRecord tickRecord) {
		try {
			tickRecord.setTimeInMillis(in.readLong());
			tickRecord.setBid(in.readDouble());
			tickRecord.setAsk(in.readDouble());
			tickRecord.setEmpty(false);
		} catch (EOFException e) {
			tickRecord.setEmpty(true);
			moreData = false;
		} catch (IOException e) {
			throw new GigawattException("GenericFXMarketDataReader: There was a problem reading market data for " + pair, e);
		}
	}

	private DataInputStream in;

	public void init() {
		try {
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
		}
		catch (FileNotFoundException e) {
			throw new GigawattException("GenericFXMarketDataReader: Could not find the file (" + filePath + ") for " + pair, e);
		}
	}

}