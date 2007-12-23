package com.dojoconsulting.oanda.fxtrade.strategy;

import com.dojoconsulting.gigawatt.strategy.IStrategy;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTest;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;
import com.dojoconsulting.oanda.fxtrade.api.OAException;
import com.dojoconsulting.oanda.fxtrade.api.RateTable;
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
public class StressTestStrategy implements IStrategy {
	private static Log logger = LogFactory.getLog(StressTestStrategy.class);

	private FXTest client;
	private FXPair pair;
	private Account primaryAccount;
	private RateTable rateTable;
	private boolean createdStress = false;
	private long startTime;

	private static final int SECS_IN_A_DAY = 1000 * 60 * 60 * 24;

	public void handle() {
		if (createdStress) {
			final long time = client.getServerTime() - startTime;
			if (time % SECS_IN_A_DAY != 0) {
				return;
			}
			final Date date = new Date(client.getServerTime());
			final String strDate = DateFormat.getDateInstance().format(date);
			logger.info(strDate);
			return;
		}
		doStrategy(primaryAccount);
	}

	private void doStrategy(final Account account) {
		try {
			final FXTick tick = rateTable.getRate(pair);
			if (tick == null || tick.getTimestamp() == 0) {
				return;
			}
			createdStress = true;
			for (int i = 1; i <= 1000; i++) {
				final MarketOrder mo = new MarketOrder();
				mo.setPair(pair);
				mo.setUnits(1);

				account.execute(mo);

				logger.info("Creating order " + i);
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
			rateTable = client.getRateTable();
		}
		catch (OAException e) {
			e.printStackTrace();
		}
	}
}