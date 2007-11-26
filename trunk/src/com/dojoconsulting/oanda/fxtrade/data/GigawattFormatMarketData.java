package com.dojoconsulting.oanda.fxtrade.data;

import com.dojoconsulting.oanda.fxtrade.api.FXPair;

import java.text.ParseException;

/**
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 07:55:52
 * GigawattFormatMarketData expects a file that has data in the format of "timestamp,bid,ask"
 */
public class GigawattFormatMarketData extends GenericFXMarketDataReader {

	public GigawattFormatMarketData(final FXPair pair, final String path) {
		super(pair, path);
	}

	protected void processRecord(final String record, final TickRecord tickRecord) throws ParseException {
		final String[] tokens = record.split(",");
		tickRecord.setTimeInMillis(Long.parseLong(tokens[0]));
		tickRecord.setBid(Double.parseDouble(tokens[1]));
		tickRecord.setAsk(Double.parseDouble(tokens[2]));
	}


}