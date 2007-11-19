package com.dojoconsulting.oanda.fxtrade.api;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 18-Nov-2007
 * Time: 20:52:36
 */
public class MarketOrderTest extends TestCase {

    public MarketOrderTest(final String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetUnrealizedPL() throws Exception {
        MarketOrder mo = new MarketOrder();

        mo.setPair(new FXPair("GBP/JPY"));
        mo.setPrice(230.00);

        mo.setUnits(10000);
        // long in profit +83
        double profit = mo.getUnrealizedPL(new FXTick(0, 230.830, 230.870));
        assertEquals("Invalid amount of UnPL for a long in profit", 8300.00, profit);
        // long in loss - 14
        profit = mo.getUnrealizedPL(new FXTick(0, 229.86, 229.90));
        assertEquals("Invalid amount of UnPL for a long in loss", -1400.00, profit);

        mo.setUnits(-10000);
        //short in profit +83
        profit = mo.getUnrealizedPL(new FXTick(0, 229.13, 229.17));
        assertEquals("Invalid amount of UnPL for a short in profit", 8300.00, profit);

        //short in loss -14
        profit = mo.getUnrealizedPL(new FXTick(0, 230.10, 230.14));
        assertEquals("Invalid amount of UnPL for a short in loss", -1400.00, profit);
    }

}
