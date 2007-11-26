package com.dojoconsulting.oanda.fxtrade.data;

import com.dojoconsulting.oanda.fxtrade.api.FXPair;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 07:55:52
 * OandaASCIITickMarketData expects a file that has data in the format of "dd/MM/yyyy hh:mm:ss bid ask"
 */
public class OandaASCIITickMarketData extends GenericFXMarketDataReader {

	final DateFormat formatter = new SimpleDateFormat("dd/MM/yy hh:mm:ss");

	public OandaASCIITickMarketData(final FXPair pair, final String path) {
		super(pair, path);
	}

	protected void processRecord(final String record, final TickRecord tickRecord) throws ParseException {
		final String[] tokens = record.split(" ");
		final Date date = formatter.parse(tokens[0] + " " + tokens[1]);
		tickRecord.setTimeInMillis(date.getTime());
		tickRecord.setBid(Double.parseDouble(tokens[2]));
		tickRecord.setAsk(Double.parseDouble(tokens[3]));
	}


}