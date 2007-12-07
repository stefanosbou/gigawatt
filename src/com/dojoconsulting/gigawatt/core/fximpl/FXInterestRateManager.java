/**
 *
 */

package com.dojoconsulting.gigawatt.core.fximpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.IInterestRateManager;
import com.dojoconsulting.gigawatt.core.fximpl.FXTradeManager.OrderMonitor;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;
import com.dojoconsulting.oanda.fxtrade.api.Position;
import com.dojoconsulting.oanda.fxtrade.api.Transaction;

/**
 * @author Nick Skaggs
 */
public class FXInterestRateManager implements IInterestRateManager {

	// Read in interest rates into your new table and to create the table
	public void init(final BackTestConfig config) {
		//TODO: Anything to init for int rate manager?
		// Comment by AmitChada: You will need to get the interest rate file and want to create the db tables.
//		long timestamp = 0;
//		if (logger.isDebugEnabled()) {
//			timestamp = System.currentTimeMillis();
//			logger.debug("FXINterestRateManager: Started init()");
//		}
//		try {
//			orderMonitors = new HashMap<String, OrderMonitor>();
//			refreshPairs = new HashSet<FXPair>();
//
//			Class.forName("org.hsqldb.jdbcDriver");
//			Connection tradeDB = DriverManager.getConnection("jdbc:hsqldb:mem:tradeDB", "sa", "");
//			final Statement st = tradeDB.createStatement();
//			//currency timeStartedInMillis bid ask
//			final String expression = "CREATE TABLE rates ( currency INTEGER IDENTITY, accountId INTEGER, market CHAR(7), isLong BOOLEAN, stopLoss FLOAT, takeProfit FLOAT)";
//			st.executeUpdate(expression);
//			st.close();
//
//			tradeInsert = tradeDB.prepareStatement("INSERT INTO trades (tradeId, accountId, market, isLong, stopLoss, takeProfit) VALUES (?, ?, ?, ?, ?, ?)");
//			tradeUpdate = tradeDB.prepareStatement("UPDATE trades SET stopLoss = ?, takeProfit = ? WHERE tradeId = ?");
//			tradeSelectLongStopLoss = tradeDB.prepareStatement("SELECT max(stopLoss) FROM trades WHERE market = ? AND isLong = 1 AND stopLoss <> 0");
//			tradeSelectLongTakeProfit = tradeDB.prepareStatement("SELECT min(takeProfit) FROM trades WHERE market = ? AND isLong = 1 and takeProfit <> 0");
//			tradeSelectShortStopLoss = tradeDB.prepareStatement("SELECT min(stopLoss) FROM trades WHERE market = ? AND isLong = 0 AND stopLoss <> 0");
//			tradeSelectShortTakeProfit = tradeDB.prepareStatement("SELECT max(takeProfit) FROM trades WHERE market = ? AND isLong = 0 and takeProfit <> 0");
//			stopLossTradesLong = tradeDB.prepareStatement("SELECT tradeId, accountId FROM trades WHERE market = ? AND isLong = 1 AND stopLoss > ?");
//			stopLossTradesShort = tradeDB.prepareStatement("SELECT tradeId, accountId FROM trades WHERE market = ? AND isLong = 0 AND stopLoss < ?");
//
//			takeProfitTradesLong = tradeDB.prepareStatement("SELECT tradeId, accountId FROM trades WHERE market = ? AND isLong = 1 AND takeProfit < ?");
//			takeProfitTradesShort = tradeDB.prepareStatement("SELECT tradeId, accountId FROM trades WHERE market = ? AND isLong = 0 AND takeProfit > ?");
//
//			tradeClose = tradeDB.prepareStatement("DELETE FROM trades WHERE tradeId = ?");
//			tradeCount = tradeDB.prepareStatement("SELECT count(*) FROM trades");
//		}
//		catch (SQLException e) {
//			e.printStackTrace();  // Todoerror: Improve error handling
//			System.exit(1);
//		}
//		catch (ClassNotFoundException e) {
//			e.printStackTrace();  // Todoerror: Improve error handling
//			System.exit(1);
//		}
//		if (logger.isDebugEnabled()) {
//			logger.debug("FXInterestManager: Finished init() in " + (System.currentTimeMillis() - timestamp));
//		}
	}

	public Transaction calcInterestForClosedTrade(final MarketOrder marketOrder) {
//		if mo.isopen()
//			return null;
//		startTime = mo.getTime();
//		if startTime < 4PM_EST then startTime = 4PM_EST;
//		endTime = mo.getClose().getTime();
//		getAllInterestRatesFromDatabaseFor(startTime, endTime, mo.pair); calculateInterest;
		return null;
	}

	public Transaction calcInterestForRolloverPosition(final Position position) {
		return null;
	}

	public Transaction calcInterestForAccount(final Account account) {
		return null;
	}

	public void registerBalanceChange(final Account account, final double oldBalance, final double newBalance) {

	}

	public void close() {
		//TODO: Anything to close for int rate manager?
		// Comment by AmitChada: Close the db connection is all I can think of
	}

}
