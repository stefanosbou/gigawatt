/**
 *
 */

package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.IInterestRateManager;
import com.dojoconsulting.gigawatt.core.TimeEvent;
import com.dojoconsulting.gigawatt.core.TimeServer;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;
import com.dojoconsulting.oanda.fxtrade.api.Position;
import com.dojoconsulting.oanda.fxtrade.api.Transaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Nick Skaggs
 */
public class FXInterestRateManager implements IInterestRateManager {

	//Variables
	private static Log logger = LogFactory.getLog(FXInterestRateManager.class);
	private DataSource dataSource;

	private PreparedStatement rateInsert;

	private TimeServer timeServer;
	private TimeEvent rolloverInterestTimeEvent = createTimeEvent();


	public void setTimeServer(final TimeServer timeServer) {
		this.timeServer = timeServer;
	}

	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// Read in interest rates into your new table and to create the table
	public void init(final BackTestConfig config) {
		//TODO: Anything to init for int rate manager?
		// Comment by AmitChada: You will need to get the interest rate file and want to create the db tables.
		long timestamp = 0;

		createRollover();

		if (logger.isDebugEnabled()) {
			timestamp = System.currentTimeMillis();
			logger.debug("FXInterestRateManager: Started init()");
		}
		try {
			final Connection irateDB = dataSource.getConnection();

			final Statement st = irateDB.createStatement();
//			//currency timeStartedInMillis bid ask
			final String expression = "CREATE TABLE irates ( currency INTEGER, timestamp FLOAT, bid FLOAT, ask FLOAT, PRIMARY KEY(currency, timestamp))";
			st.executeUpdate(expression);
			st.close();

			rateInsert = irateDB.prepareStatement("INSERT INTO irates (currency, timestamp, bid, ask) VALUES (?, ?, ?, ?)");

//			tradeSelectLongTakeProfit = tradeDB.prepareStatement("SELECT min(takeProfit) FROM trades WHERE market = ? AND isLong = 1 and takeProfit <> 0");
//			tradeSelectShortStopLoss = tradeDB.prepareStatement("SELECT min(stopLoss) FROM trades WHERE market = ? AND isLong = 0 AND stopLoss <> 0");

			//Load db from Interest Rate History file
			loadIrateHistory();

			//Register time event

		}
		catch (SQLException e) {
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

	public Transaction calcInterestForPosition(final Position position) {
		//account sends position, return interest
		return null;
	}

	public void calcInterestForRollover() {
//		get all the accounts from the IAccountManager, and loop over them and call calcInterestForRolloverPosition(account)
//		final Account account = accountManager.getAccountWithId(accountNumber);
		logger.info("Calculating rollover for accounts");
	}

	public Transaction calcInterestForAccount(final Account account) {
		//calculate interest from trades in trade manager
		//getPositions() with account from account.java
//		static final positions = account.getPositions();
		return null;
	}

	public void registerBalanceChange(final Account account, final double oldBalance, final double newBalance) {

	}

	public void close() {
		//TODO: Anything to close for int rate manager?
		// Comment by AmitChada: Close the db connection is all I can think of
	}

	private TimeEvent createTimeEvent() {
		return new TimeEvent() {
			public void handle(final long timeForEvent) {
				calcInterestForRollover();
			}
		};
	}

	private void createRollover() {
		rolloverInterestTimeEvent.setTimeForEvent(timeServer.getTimeInMilliFor("16:00:00"));
		rolloverInterestTimeEvent.setRecurrence(TimeServer.DAY_IN_MILLI);
		timeServer.addTimeEvent(rolloverInterestTimeEvent);
	}

	private void loadIrateHistory() {
		//Ref: OandaToGigawattTickConvertor, load db from ascii file
		
	}
}
