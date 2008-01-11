/**
 *
 */

package com.dojoconsulting.gigawatt.core.fximpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.IInterestRateManager;
//import com.dojoconsulting.gigawatt.core.fximpl.FXTradeManager.OrderMonitor;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;
import com.dojoconsulting.oanda.fxtrade.api.Position;
import com.dojoconsulting.oanda.fxtrade.api.Transaction;

/**
 * @author Nick Skaggs
 */
public class FXInterestRateManager implements IInterestRateManager {
	
	//Variables
	private static Log logger = LogFactory.getLog(FXTradeManager.class);
	private PreparedStatement irateInsert;

	// Read in interest rates into your new table and to create the table
	public void init(final BackTestConfig config) {
		//TODO: Anything to init for int rate manager?
		// Comment by AmitChada: You will need to get the interest rate file and want to create the db tables.
		long timestamp = 0;
		if (logger.isDebugEnabled()) {
			timestamp = System.currentTimeMillis();
			logger.debug("FXINterestRateManager: Started init()");
		}
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			Connection irateDB = DriverManager.getConnection("jdbc:hsqldb:mem:irateDB", "sa", "");
			final Statement st = irateDB.createStatement();
//			//currency timeStartedInMillis bid ask
			final String expression = "CREATE TABLE irates ( currency INTEGER IDENTITY, time FLOAT, bid FLOAT, ask FLOAT)"; 
			st.executeUpdate(expression);
			st.close();

//			irateInsert = irateDB.prepareStatement("INSERT INTO irates (currency, time, bid, ask,) VALUES (?, ?, ?, ?)");

//			tradeSelectLongTakeProfit = tradeDB.prepareStatement("SELECT min(takeProfit) FROM trades WHERE market = ? AND isLong = 1 and takeProfit <> 0");
//			tradeSelectShortStopLoss = tradeDB.prepareStatement("SELECT min(stopLoss) FROM trades WHERE market = ? AND isLong = 0 AND stopLoss <> 0");
		}
		catch (SQLException e) {
			e.printStackTrace();  // Todoerror: Improve error handling
			System.exit(1);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();  // Todoerror: Improve error handling
			System.exit(1);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("FXInterestManager: Finished init() in " + (System.currentTimeMillis() - timestamp));
		}
	}

	public Transaction calcInterestForClosedTrade(final MarketOrder mo) {
		//if mo.isopen()
		//	return null;
//		startTime = mo.getTimestamp();
//		if startTime < 4PM_EST {
//			startTime = 4PM_EST;
//		}
//		endTime = mo.getClose().getTime();
//		getAllInterestRatesFromDatabaseFor(startTime, endTime, mo.pair);
//		calculateInterest;
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
