package com.dojoconsulting.oanda.fxtrade.data;

import com.dojoconsulting.oanda.fxtrade.api.FXPair;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 07:55:52
 * OandaASCIITickMarketData expects a file that has data in the format of "dd/MM/yyyy hh:mm:ss bid ask"
 */
public class OandaASCIITickMarketData extends GenericFXMarketDataReader {

	public OandaASCIITickMarketData(final FXPair pair, final String path) {
		super(pair, path);
	}

	protected String[] processRecord(final String record) {
		final String[] tokens = record.split(" ");
		return new String[]{tokens[0] + " " + tokens[1], tokens[2], tokens[3]};
	}

	protected DateFormat getDateFormatter() {
		return new SimpleDateFormat("dd/MM/yy hh:mm:ss");
	}

}