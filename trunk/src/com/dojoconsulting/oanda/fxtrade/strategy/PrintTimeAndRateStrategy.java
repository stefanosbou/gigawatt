package com.dojoconsulting.oanda.fxtrade.strategy;

import com.dojoconsulting.gigawatt.strategy.IStrategy;
import com.dojoconsulting.oanda.fxtrade.api.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 07:59:14
 * To change this template use File | Settings | File Templates.
 */
public class PrintTimeAndRateStrategy implements IStrategy {
    private static Log logger = LogFactory.getLog(PrintTimeAndRateStrategy.class);

    private FXTest client;
    private FXPair pair;

    public void handle() {
        final long time = client.getServerTime();
        final RateTable table;
        try {
            table = client.getRateTable();
            final FXTick tick = table.getRate(pair);
            final String message = time + " " + tick.getTimestamp() + " " + tick.getBid();

//			System.out.println(message);
        }
        catch (SessionDisconnectedException e) {
            e.printStackTrace();
        }
        catch (RateTableException e) {
            e.printStackTrace();
        }

    }

    public void init() {
        client = new FXTest();
        pair = new FXPair("GBP/JPY");
    }
}
