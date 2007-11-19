package com.dojoconsulting.oanda.fxtrade.api;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * FXPair Tester.
 *
 * @author Amit Chada
 * @version 1.0
 * @since 10/27/2007
 */
public class FXPairTest extends TestCase {


	public FXPairTest(final String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testIsMajor() {
		final String[] majors = new String[]{"AUD", "CAD", "CHF", "EUR", "GBP", "JPY", "USD", "NOK", "NZD", "SEK", "DKK", "XAG", "XAU"};
		// Test majors
		for (int i = 0; i < majors.length; i++) {
			for (int j = 0; j < majors.length; j++) {
				if (i == j) {
					continue;
				}
				final String base = majors[i];
				final String quote = majors[j];
				final FXPair pair = new FXPair(base, quote);
				assertEquals("Incorrect value for isMajor when using " + base + "/" + quote, true, pair.isMajor());
			}
		}
		final String exotic = "TRY";
		// Test majors as base and quote
		for (final String major : majors) {
			FXPair pair = new FXPair(major, exotic);
			assertEquals("Incorrect value for isMajor when using " + major + "/" + exotic, false, pair.isMajor());
			pair = new FXPair(exotic, major);
			assertEquals("Incorrect value for isMajor when using " + exotic + "/" + major, false, pair.isMajor());
		}
		// Test pure exotic
		final String anotherExotic = "HKD";
		final FXPair pair = new FXPair(anotherExotic, exotic);
		assertEquals("Incorrect value for isMajor when using " + anotherExotic + "/" + exotic, false, pair.isMajor());
	}

	public static Test suite() {
		return new TestSuite(FXPairTest.class);
	}
}
