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

		try {
			final FXTick tick = rateTable.getRate(pair);
			if (tick == null || tick.getTimestamp() == 0) {
				return;
			}
			final Position p = account.getPosition(pair);
			if (p != null) {
				final double buyPrice = p.getPrice();
				final double sellPrice = tick.getBid();
				final double pips = UtilMath.round((sellPrice - buyPrice) * 10000, 1);
				if (pips > 0) {
					System.out.println("Selling position at profit of " + pips + " pips on " + p.getUnits() + " units");
					account.close(pair.getPair());
				}
				if (pips < 0 && pips > -100) {
					return;
				}
			}
			final long units = (long) (account.getBalance() * 50 / 100);
			final MarketOrder mo = new MarketOrder();
			mo.setPair(pair);
			mo.setUnits(units);
			
			final Date date = new Date(client.getServerTime());
			final String strDate = DateFormat.getDateInstance().format(date);
			final String message;
			
			if (account.execute(mo)) {
				message = strDate + ": Bought " + units + " units at " + tick.getAsk();
				System.out.println(message);
//				System.out.println(account.toString());
			} 
			else {
				message = strDate + ": Order failed";
				System.out.println(message);
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