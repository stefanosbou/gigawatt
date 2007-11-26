package com.dojoconsulting.gigawatt.tools;

import com.dojoconsulting.gigawatt.core.GigawattException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
		final BufferedWriter out;

		final RecordProcessor processor = mode.equals("ASCII") ? new AsciiRecordProcessor() : new CsvRecordProcessor();
		final DateFormat formatter = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
		String currentToken = "";
		try {
			in = new BufferedReader(new FileReader(from));
			out = new BufferedWriter(new FileWriter(to));
			while (in.ready()) {
				final String dataRecord = in.readLine();
				if (dataRecord == null) {
					break;
				}
				final String[] tokens = processor.processRecord(dataRecord);
				currentToken = tokens[0];
				final Date date = formatter.parse(currentToken);
				final long millis = date.getTime();
				final String bid = tokens[1];
				final String ask = tokens[2];
				out.write(millis + CSV_DELIMITER + bid + CSV_DELIMITER + ask + "\n");
			}
			out.flush();
			out.close();
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
