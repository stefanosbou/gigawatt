package com.dojoconsulting.oanda.fxtrade.api;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Map;

/**
 * UtilMath Tester.
 *
 * @author <Authors name>
 * @since <pre>10/27/2007</pre>
 * @version 1.0
 */
public class UtilMathTest extends TestCase {
    public UtilMathTest(final String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

	public void testMarginPercentageRequired() {
		final FXPair major = new FXPair("EUR/USD");
		final FXPair exotic = new FXPair("EUR/TRY");
		final int[] leverages = new int[]{10, 20, 25, 30, 40, 50};
		final double[] majorResults = new double[]{0.10, 0.05, 0.04, 0.033333, 0.025, 0.02};
		final double[] exoticResults = new double[]{0.10, 0.05, 0.04, 0.04, 0.04, 0.04};

		for (int i = 0; i < leverages.length; i++) {
			final int leverage = leverages[i];
			//test major
			double result = UtilMath.marginPercentageRequired(major, leverage);
			assertEquals("Incorrect marginPercentageRequired for Majors (" + major + ", " + leverage + ")", majorResults[i], result);
			//test exotic
			result = UtilMath.marginPercentageRequired(exotic, leverage);
			assertEquals("Incorrect marginPercentageRequired for Majors (" + exotic + ", " + leverage + ")", exoticResults[i], result);
		}
	}


	public void testCalculatePositionValue() {
		final String homeCurrency = "USD";
		final Map<FXPair, FXTick> tickTable = new HashMap<FXPair, FXTick>();
		FXPair pair = new FXPair("USD/CAD");
		double result = UtilMath.calculatePositionValue(pair, 15000, homeCurrency, tickTable);
		assertEquals("Incorrect position value  for (" + pair+ ", 15000, " + homeCurrency + ")", 15000.0, result);

		pair = new FXPair("USD/EUR");
		result = UtilMath.calculatePositionValue(pair, 10000, homeCurrency, tickTable);
		assertEquals("Incorrect position value  for (" + pair+ ", 10000, " + homeCurrency + ")", 10000.0, result);

		pair = new FXPair("EUR/USD");
		tickTable.put((FXPair) pair.clone(), new FXTick(0, 0.9134, 0.9136));
		result = UtilMath.calculatePositionValue(pair, 10000, homeCurrency, tickTable);
		assertEquals("Incorrect position value  for (" + pair+ ", 10000, " + homeCurrency + ")", 9136.0, result);

		pair = new FXPair("EUR/CZK");
		result = UtilMath.calculatePositionValue(pair, -20000, homeCurrency, tickTable);
		assertEquals("Incorrect position value  for (" + pair+ ", -20000, " + homeCurrency + ")", 18268.0, result);

		//TodoTest: add test for using interim USD rates
	}
    public static Test suite() {
        return new TestSuite(UtilMathTest.class);
    }
}
