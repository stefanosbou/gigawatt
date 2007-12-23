package com.dojoconsulting.oanda.fxtrade.strategy;

import com.dojoconsulting.gigawatt.strategy.IStrategy;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTest;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;
import com.dojoconsulting.oanda.fxtrade.api.LimitOrder;
import com.dojoconsulting.oanda.fxtrade.api.OAException;
import com.dojoconsulting.oanda.fxtrade.api.RateTable;
import com.dojoconsulting.oanda.fxtrade.api.TakeProfitOrder;
import com.dojoconsulting.oanda.fxtrade.api.User;
import com.dojoconsulting.oanda.fxtrade.api.UtilMath;
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
public class LimitStraddleStrategy implements IStrategy {
	private static Log logger = LogFactory.getLog(LimitStraddleStrategy.class);

	private FXTest client;
	private FXPair pair;
	private Account account;
	private long startTime;
	private RateTable rateTable;

	private static final int SECS_IN_TEN_DAYS = 1000 * 60 * 60 * 24 * 10;
	private static final double SPACE = 0.0010;

	public void handle() {
		final long time = client.getServerTime() - startTime;
		if (time % SECS_IN_TEN_DAYS != 0) {
			return;
		}

		try {
			final FXTick tick = rateTable.getRate(pair);
			if (tick == null) {
				return;
			}
			final long units = (long) (account.getBalance() * 50 / 100);

			for (int i = 1; i < 11; i++) {
				final LimitOrder longLO = new LimitOrder(SECS_IN_TEN_DAYS + client.getServerTime());
				final LimitOrder shortLO = new LimitOrder(SECS_IN_TEN_DAYS + client.getServerTime());


				longLO.setPair(pair);
				longLO.setUnits(units);
				shortLO.setPair(pair);
				shortLO.setUnits(units * -1);

				final double price = tick.getMean();
				final double offset = i * SPACE;
				longLO.setPrice(price + offset);
				shortLO.setPrice(price - offset);

				final TakeProfitOrder longTakeProfit = new TakeProfitOrder();
				longTakeProfit.setPrice(price + offset + SPACE);
				longLO.setTakeProfit(longTakeProfit);

				final TakeProfitOrder shortTakeProfit = new TakeProfitOrder();
				shortTakeProfit.setPrice(price - offset - SPACE);
				shortLO.setTakeProfit(shortTakeProfit);

				if (logger.isInfoEnabled()) {
					final Date date = new Date(client.getServerTime());
					final String strDate = DateFormat.getDateInstance().format(date);

					String message = strDate + ": Setup long order of " + units + " units at " + UtilMath.round((price + offset), 6);
					logger.info(message);
					message = strDate + ": Setup short order of " + units + " units at " + UtilMath.round((price - offset), 6);
					logger.info(message);
				}

				account.execute(longLO);
				account.execute(shortLO);
			}
		}
		catch (OAException e) {
			e.printStackTrace();
		}

	}

	public void init() {
		try {
			client = new FXTest();
			client.login("SomeUserName", "somepassword");
			pair = new FXPair("GBP/USD");
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