package com.dojoconsulting.gigawatt.tools;

import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.oanda.fxtrade.api.UtilMath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 26-Nov-2007
 * Time: 03:31:18
 */
public class OandaToGigawattTickConvertor {
	private static final String CSV_DELIMITER = ",";
	private static final String ASCII_DELIMITER = " ";
	private static final String USAGE = "Usage: java OandaToGigawattTickConvertor <ASCII|CSV> <fromFile> <toFile>";
	private static Log logger = LogFactory.getLog(OandaToGigawattTickConvertor.class);

	public static void main(final String[] args) {
		if (args.length != 3) {
			System.err.println(USAGE);
			System.exit(1);
		}

		final String mode = args[0];
		final String from = args[1];
		final String to = args[2];

		if (!mode.equalsIgnoreCase("ASCII") && !mode.equalsIgnoreCase("CSV")) {
			System.err.println(USAGE);
			System.exit(1);
		}

		new OandaToGigawattTickConvertor().convert(mode, from, to);

	}

	public void convert(final String mode, final String from, final String to) {
		final BufferedReader in;
		final DataOutputStream out;

		int totalCounter = 0;
		int discardedCounter = 0;
		int includedCounter = 0;

		double lastBid = 0;
		double lastAsk = 0;

		final RecordProcessor processor = mode.equals("ASCII") ? new AsciiRecordProcessor() : new CsvRecordProcessor();
		final DateFormat formatter = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
		String currentToken = "";
		try {
			in = new BufferedReader(new FileReader(from));
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(to)));
			while (in.ready()) {
				final String dataRecord = in.readLine();
				if (dataRecord == null) {
					break;
				}
				final String[] tokens = processor.processRecord(dataRecord);
				currentToken = tokens[0];
				final Date date = formatter.parse(currentToken);
				final long millis = date.getTime();
				final double bid = UtilMath.round(Double.parseDouble(tokens[1]), 6);
				final double ask = UtilMath.round(Double.parseDouble(tokens[2]), 6);
				if (lastBid == bid && lastAsk == ask) {
					discardedCounter += 1;
				} else {
					includedCounter += 1;
					out.writeLong(millis);
					out.writeDouble(bid);
					out.writeDouble(ask);
					if (logger.isDebugEnabled()) {
						logger.debug("Writing bid (" + bid + ") from token  " + tokens[1]);
						logger.debug("Writing ask (" + ask + ") from token  " + tokens[2]);
					}
					lastBid = bid;
					lastAsk = ask;
				}
				totalCounter += 1;
				if (logger.isInfoEnabled()) {
					if (totalCounter % 100000 == 0) {
						logger.info(totalCounter + " processed. (Included:" + includedCounter + ", Discarded:" + discardedCounter + ") ...");
					}
				}
			}
			out.flush();
			out.close();
			logger.info(totalCounter + " processed. (Included:" + includedCounter + ", Discarded:" + discardedCounter + ")");
		}
		catch (FileNotFoundException e) {
			throw new GigawattException("OandaToGigawattTickConvertor: Could not find the file (" + from + ") for conversion", e);
		}
		catch (IOException e) {
			throw new GigawattException("OandaToGigawattTickConvertor: Could not write to file (" + to + ") for conversion", e);
		}
		catch (ParseException e) {
			throw new GigawattException("OandaToGigawattTickConvertor: There was a problem parsing the date in " + from + " with the following data: '" + currentToken + "'.", e);
		}
	}

	interface RecordProcessor {
		String[] processRecord(final String dataRecord);
	}

	class AsciiRecordProcessor implements RecordProcessor {
		public String[] processRecord(final String dataRecord) {
			final String[] tokens = dataRecord.split(ASCII_DELIMITER);
			return new String[]{tokens[0] + " " + tokens[1], tokens[2], tokens[3]};
		}
	}

	class CsvRecordProcessor implements RecordProcessor {
		public String[] processRecord(final String dataRecord) {
			final String[] tokens = dataRecord.split(CSV_DELIMITER);
			return new String[]{tokens[0], tokens[1], tokens[2]};
		}
	}
}
