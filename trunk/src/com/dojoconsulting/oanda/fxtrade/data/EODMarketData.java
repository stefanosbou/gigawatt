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
 * EODMarketData expects a file that has data in the format of dd.mm.yyyy,price
 */
public class EODMarketData extends AbstractFileMarketDataReader {

	final DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

	public EODMarketData(final FXPair pair, final String path) {
		super(pair, path);
	}

	protected void processRecord(final String record, final TickRecord tickRecord) throws ParseException {
		final String[] tokens = record.split(",");
		final Date date = formatter.parse(tokens[0]);
		tickRecord.setTimeInMillis(date.getTime());
		final double price = Double.parseDouble(tokens[1]);
		tickRecord.setBid(price);
		tickRecord.setAsk(price);
	}
}
