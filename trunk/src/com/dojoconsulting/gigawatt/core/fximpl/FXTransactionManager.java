package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.gigawatt.core.ITransactionManager;
import com.dojoconsulting.gigawatt.core.fximpl.domain.TransactionType;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.AccountException;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;
import com.dojoconsulting.oanda.fxtrade.api.Transaction;
import com.dojoconsulting.oanda.fxtrade.api.UtilAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: Amit
 * Date: 01-Dec-2007
 * Time: 18:11:53
 */
public class FXTransactionManager implements ITransactionManager {

	private static Log logger = LogFactory.getLog(FXTransactionManager.class);

	private int nextTicketNumber = 1;
	private DataSource dataSource;
	private PreparedStatement transactionInsert;

	private static final String BUY_MARKET = "BuyMarket";
	private static final String SELL_MARKET = "SellMarket";

	public void init(final BackTestConfig config) {
		long timestamp = 0;
		if (logger.isDebugEnabled()) {
			timestamp = System.currentTimeMillis();
			logger.debug("FXTransactionManager: Started init()");
		}
		try {
			final Connection orderDB = dataSource.getConnection();

			final Statement st = orderDB.createStatement();
			st.executeUpdate("DROP TABLE transactions IF EXISTS");
			final String expression = "CREATE TABLE transactions ( transactionNumber INTEGER, accountId INTEGER, date TIMESTAMP, timestamp BIGINT, expiry BIGINT, pair VARCHAR(7), transactionType VARCHAR, completionCode INTEGER, price FLOAT, units INTEGER, stoploss FLOAT, takeprofit FLOAT, tranlink INTEGER, lowerbound FLOAT, upperbound FLOAT, interest FLOAT, pl FLOAT, amount FLOAT, balance FLOAT)";
			st.executeUpdate(expression);
			st.close();

			transactionInsert = orderDB.prepareStatement("INSERT INTO transactions (transactionNumber, accountId, date, timestamp, expiry, pair, transactionType, completionCode, price, units, stoploss, takeprofit, tranlink, lowerbound, upperbound, interest, pl, amount, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		}
		catch (SQLException e) {
			e.printStackTrace();  // Todoerror: Improve error handling
			System.exit(1);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("FXTransactionManager: Finished init() in " + (System.currentTimeMillis() - timestamp));
		}
	}

	public void close() {
	}

	public int getNextTransactionNumber() {
		return nextTicketNumber++;
	}

	public Transaction createExecuteMarketTransaction(final MarketOrder mo, final Account account, final TransactionType transactionType) {
		try {
			final double tp = (mo.getTakeProfit() == null ? 0 : mo.getTakeProfit().getPrice());
			final double sl = (mo.getStopLoss() == null ? 0 : mo.getStopLoss().getPrice());
			final double margin = 0;
			final long units = Math.abs(mo.getUnits());
			final String type = mo.isBuy() ? BUY_MARKET : SELL_MARKET;
			final Transaction t = UtilAPI.createTransaction(0, account.getBalance(), transactionType.getCompletionCode(), 0, 0, margin, mo.getPair(), mo.getPrice(), sl, tp, mo.getTimestamp(), 0, mo.getTransactionNumber(), units, type, 0, mo.getLowPriceLimit(), mo.getHighPriceLimit(), 0);
			persist(t, account);
			return t;
		} catch (AccountException e) {
			throw new GigawattException("Error encountered when attempting to create Execute Transaction in FXTransactionManager for MarketOrder " + mo + " with transactionType " + transactionType + " for account " + account.getAccountId());
		}
	}

	public Transaction createCloseTransaction(final MarketOrder mo, final Account account, final TransactionType transactionType, double realizedPL, double interest) {
		try {
			final double tp = (mo.getTakeProfit() == null ? 0 : mo.getTakeProfit().getPrice());
			final double sl = (mo.getStopLoss() == null ? 0 : mo.getStopLoss().getPrice());
			final double margin = 0;
			final long units = Math.abs(mo.getUnits());
			final String type = transactionType.getCloseType();
			final Transaction t = UtilAPI.createTransaction(realizedPL + interest, account.getBalance(), transactionType.getCompletionCode(), 0, interest, margin, mo.getPair(), mo.getClose().getPrice(), sl, tp, mo.getClose().getTimestamp(), mo.getClose().getTransactionLink(), mo.getClose().getTransactionNumber(), units, type, 0, mo.getLowPriceLimit(), mo.getHighPriceLimit(), realizedPL);
			persist(t, account);
			return t;
		} catch (AccountException e) {
			throw new GigawattException("Error encountered when attempting to create Execute Transaction in FXTransactionManager for MarketOrder " + mo + " with transactionType " + transactionType + " for account " + account.getAccountId());
		}
	}

	private void persist(final Transaction t, final Account account) {
		try {
			transactionInsert.clearParameters();
			transactionInsert.setInt(1, t.getTransactionNumber());
			transactionInsert.setInt(2, account.getAccountId());
			transactionInsert.setTimestamp(3, new Timestamp(t.getTimestamp()));
			transactionInsert.setLong(4, t.getTimestamp());
			transactionInsert.setLong(5, t.getExpiry());
			transactionInsert.setString(6, t.getPair().getPair());
			transactionInsert.setString(7, t.getType());
			transactionInsert.setInt(8, t.getCompletionCode());
			transactionInsert.setDouble(9, t.getPrice());
			transactionInsert.setLong(10, t.getUnits());
			transactionInsert.setDouble(11, t.getStopLoss());
			transactionInsert.setDouble(12, t.getTakeProfit());
			transactionInsert.setLong(13, t.getTransactionLink());
			transactionInsert.setDouble(14, t.getLowerBound());
			transactionInsert.setDouble(15, t.getUpperBound());
			transactionInsert.setDouble(16, t.getInterest());
			transactionInsert.setDouble(17, t.getProfitLoss());
			transactionInsert.setDouble(18, t.getAmount());
			transactionInsert.setDouble(19, t.getBalance());
			transactionInsert.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
