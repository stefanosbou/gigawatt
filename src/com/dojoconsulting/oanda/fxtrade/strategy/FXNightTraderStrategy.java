package com.dojoconsulting.oanda.fxtrade.strategy;

import com.dojoconsulting.gigawatt.strategy.IStrategy;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTest;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;
import com.dojoconsulting.oanda.fxtrade.api.OAException;
import com.dojoconsulting.oanda.fxtrade.api.RateTable;
import com.dojoconsulting.oanda.fxtrade.api.StopLossOrder;
import com.dojoconsulting.oanda.fxtrade.api.TakeProfitOrder;
import com.dojoconsulting.oanda.fxtrade.api.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 07:59:14
 */
public class FXNightTraderStrategy implements IStrategy {
	private static Log logger = LogFactory.getLog(FXNightTraderStrategy.class);

	private FXTest client;
	private FXPair pair;
	private Account account;
	private long startTime;
	private RateTable rateTable;

	private static final int SECS_IN_A_DAY = 1000 * 60 * 60 * 24;

	public void handle() {
		final long time = client.getServerTime() - startTime;
		if (time % SECS_IN_A_DAY != 0) {
			return;
		}

		final Date date = new Date(client.getServerTime());
		final String strDate = DateFormat.getDateInstance().format(date);

		final RateTable table;
		try {
			final FXTick tick = rateTable.getRate(pair);
			if (tick == null) {
				return;
			}
			final String message = strDate + ": Bought 500 units at " + tick.getBid();
			final MarketOrder mo = new MarketOrder();
			mo.setPair(pair);
			mo.setUnits(500);
			final StopLossOrder stopLoss = new StopLossOrder();
			stopLoss.setPrice(tick.getAsk() - 20);
			mo.setStopLoss(stopLoss);
			final TakeProfitOrder takeProfit = new TakeProfitOrder();
			takeProfit.setPrice(tick.getAsk() + 1);
			mo.setTakeProfit(takeProfit);
			account.execute(mo);
			System.out.println(message);
			System.out.println(account.toString());
		}
		catch (OAException e) {
			e.printStackTrace();
		}

	}

	public void init() {
		try {
			client = new FXTest();
			client.login("SomeUserName", "somepassword");
			pair = new FXPair("GBP/JPY");
			final User user = client.getUser();
			account = user.getAccountWithId(1234);
			startTime = client.getServerTime();
			rateTable = client.getRateTable();
		}
		catch (OAException e) {
			e.printStackTrace();
		}
	}
}