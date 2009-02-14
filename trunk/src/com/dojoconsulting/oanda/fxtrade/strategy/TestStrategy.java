package com.dojoconsulting.oanda.fxtrade.strategy;

import com.dojoconsulting.gigawatt.strategy.IStrategy;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTest;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;
import com.dojoconsulting.oanda.fxtrade.api.OAException;
import com.dojoconsulting.oanda.fxtrade.api.Position;
import com.dojoconsulting.oanda.fxtrade.api.RateTable;
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
public class TestStrategy implements IStrategy {
	private static Log logger = LogFactory.getLog(TestStrategy.class);

	private FXTest client;
	private FXPair pair;
	private Account primaryAccount;
	private Account hedgeAccount;
	private long startTime;
	private RateTable rateTable;

	private static final int TEN_MINUTE = 1000 * 60 * 10;

	public void handle() {
		final long time = client.getServerTime() - startTime;
		if (time % TEN_MINUTE != 0) {
			return;
		}
		doStrategy(primaryAccount, true);
		doStrategy(hedgeAccount, false);
	}

	private void doStrategy(final Account account, final boolean goLong) {
		try {
			final FXTick tick = rateTable.getRate(pair);
			if (tick == null || tick.getTimestamp() == 0) {
				return;
			}
			final Position p = account.getPosition(pair);
			if (p != null) {
				final double buyPrice = p.getPrice();
				final double sellPrice = (goLong ? tick.getBid() : tick.getAsk());
				final double dif = (goLong ? sellPrice - buyPrice : buyPrice - sellPrice);
				final double pips = UtilMath.round(dif * 10000, 1);
				if (pips > 0) {
					if (logger.isInfoEnabled()) {
						logger.info("Selling position at profit of " + pips + " pips on " + p.getUnits() + " units on Account " + account.getAccountId());
					}
					account.close(pair.getPair());
				}
				if (pips < 0 && pips > -100) {
					return;
				}
			}
			long units = (long) (account.getBalance() * 50 / 100);
			if (!goLong) {
				units *= -1;
			}
			final MarketOrder mo = new MarketOrder();
			mo.setPair(pair);
			mo.setUnits(units);

			account.execute(mo);

			if (logger.isInfoEnabled()) {
				final Date date = new Date(client.getServerTime());
				final String strDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
				final String message;
				message = strDate + ": " + (goLong ? "Long " : "Short ") + units + " units at " + (goLong ? tick.getAsk() : tick.getBid());
				logger.info(message);
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
			primaryAccount = user.getAccountWithId(1234);
			hedgeAccount = user.getAccountWithId(4321);
			startTime = client.getServerTime();
			rateTable = client.getRateTable();
		}
		catch (OAException e) {
			e.printStackTrace();
		}
	}
}