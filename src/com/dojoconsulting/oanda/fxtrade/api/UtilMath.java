package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.gigawatt.core.fximpl.domain.FXPairFlyweightFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 27-Oct-2007
 * Time: 02:39:40
 */
public class UtilMath {

	private static final FXTick PARITY_TICK = new FXTick(0, 1.0, 1.0);

	private static long[] factors = new long[10];
	private static final FXPairFlyweightFactory fxPairFactory = FXPairFlyweightFactory.getInstance();

	static {
		for (int i = 0; i < 10; i++) {
			factors[i] = (long) Math.pow(10, i);
		}
	}

	/**
	 * Round a double value to a specified number of decimal
	 * places.
	 *
	 * @param val	the value to be rounded.
	 * @param places the number of decimal places to round to.
	 * @return val rounded to places decimal places.
	 */
	public static double round(double val, final int places) {
		final long factor;
		if (places < 10) {
			factor = factors[places];
		} else {
			factor = (long) Math.pow(10, places);
		}

		// Shift the decimal the correct number of places
		// to the right.
		val = val * factor;

		// Round to the nearest integer.
		val = val + 0.5;
		final long tmp = (long) val;

		// Shift the decimal the correct number of places
		// back to the left.
		return (double) tmp / factor;
	}

	static double marginPercentageRequired(final FXPair pair, final int leverage) {
		if (!pair.isMajor() && leverage >= 25) {
			return 0.04;
		}
		if (pair.isMajor() && leverage == 30) {
			return Double.parseDouble("0.033333");
		}
		return 1 / (double) leverage;
	}

	public static double calculatePositionValue(final FXPair pair, final long units, final String homeCurrency, final Map tickTable) {
		final String baseCurrency = pair.getBase();
		final FXTick tick = getConverstionRate(baseCurrency, homeCurrency, tickTable);
		double result = 0;
		if (units > 0) {
			result = units * tick.getAsk();
		} else if (units < 0) {
			result = units * tick.getBid() * -1;
		}
		return result;
	}

	public static FXTick getConverstionRate(final String base, final String quote, final Map tickTable) {
		if (base.equals(quote)) {
			return PARITY_TICK;
		}
		FXPair pair = fxPairFactory.getPair(base, quote);
		FXTick tick = (FXTick) tickTable.get(pair);
		if (tick != null) {
			return tick;
		}
		pair = pair.getInverse();
		tick = (FXTick) tickTable.get(pair);
		if (tick != null) {
			return new FXTick(tick.getTimestamp(), 1 / tick.getBid(), 1 / tick.getAsk());
		}
		if (!base.equals("USD") && !quote.equals("USD")) {
			final FXTick baseUSD = getConverstionRate(base, "USD", tickTable);
			final FXTick USDquote = getConverstionRate("USD", quote, tickTable);
			if (baseUSD != null && USDquote != null) {
				return new FXTick(0, baseUSD.getBid() * USDquote.getBid(), baseUSD.getAsk() * USDquote.getAsk());
			}
		} else {
			return null;
		}
		throw new GigawattException("Was unable to find any rate to convert " + base + " and " + quote);
	}
}
