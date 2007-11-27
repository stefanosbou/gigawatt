package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.IAccountManager;
import com.dojoconsulting.gigawatt.core.IMarketManager;
import com.dojoconsulting.gigawatt.core.ITradeManager;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;
import com.dojoconsulting.oanda.fxtrade.api.OAException;
import com.dojoconsulting.oanda.fxtrade.api.UtilAPI;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 13-Oct-2007
 * Time: 02:07:15
 */
public class FXTradeManager implements ITradeManager {

	private Log logger = LogFactory.getLog(FXTradeManager.class);
	private PreparedStatement tradeInsert;
	private PreparedStatement tradeUpdate;
	private PreparedStatement tradeClose;
	private PreparedStatement tradeSelectShortTakeProfit;
	private PreparedStatement tradeSelectLongStopLoss;
	private PreparedStatement tradeSelectLongTakeProfit;
	private PreparedStatement tradeSelectShortStopLoss;

	private PreparedStatement takeProfitTradesLong;
	private PreparedStatement takeProfitTradesShort;
	private PreparedStatement stopLossTradesLong;
	private PreparedStatement stopLossTradesShort;

	private PreparedStatement tradeCount;
	private Map<String, OrderMonitor> orderMonitors;
	private FXMarketManager marketManager;

	private Set<FXPair> refreshPairs;

	private static int nextTicketNumber = 1;
	private FXAccountManager accountManager;

	public static int getNextTicketNumber() {
		return nextTicketNumber++;
	}


	public void setMarketManager(final IMarketManager marketManager) {
		this.marketManager = (FXMarketManager) marketManager;
	}

	public void init(final BackTestConfig config) {
		long timestamp = 0;
		if (logger.isDebugEnabled()) {
			timestamp = System.currentTimeMillis();
			logger.debug("FXTradeManager: Started init()");
		}
		try {
			orderMonitors = new HashMap<String, OrderMonitor>();
			refreshPairs = new HashSet<FXPair>();

			Class.forName("org.hsqldb.jdbcDriver");
			Connection tradeDB = DriverManager.getConnection("jdbc:hsqldb:mem:tradeDB", "sa", "");
			final Statement st = tradeDB.createStatement();
			final String expression = "CREATE TABLE trades ( tradeId INTEGER IDENTITY, accountId INTEGER, market CHAR(7), isLong BOOLEAN, stopLoss FLOAT, takeProfit FLOAT)";
			st.executeUpdate(expression);
			st.close();

			tradeInsert = tradeDB.prepareStatement("INSERT INTO trades (tradeId, accountId, market, isLong, stopLoss, takeProfit) VALUES (?, ?, ?, ?, ?, ?)");
			tradeUpdate = tradeDB.prepareStatement("UPDATE trades SET stopLoss = ?, takeProfit = ? WHERE tradeId = ?");
			tradeSelectLongStopLoss = tradeDB.prepareStatement("SELECT max(stopLoss) FROM trades WHERE market = ? AND isLong = 1 AND stopLoss <> 0");
			tradeSelectLongTakeProfit = tradeDB.prepareStatement("SELECT min(takeProfit) FROM trades WHERE market = ? AND isLong = 1 and takeProfit <> 0");
			tradeSelectShortStopLoss = tradeDB.prepareStatement("SELECT min(stopLoss) FROM trades WHERE market = ? AND isLong = 0 AND stopLoss <> 0");
			tradeSelectShortTakeProfit = tradeDB.prepareStatement("SELECT max(takeProfit) FROM trades WHERE market = ? AND isLong = 0 and takeProfit <> 0");
			stopLossTradesLong = tradeDB.prepareStatement("SELECT tradeId, accountId FROM trades WHERE market = ? AND isLong = 1 AND stopLoss > ?");
			stopLossTradesShort = tradeDB.prepareStatement("SELECT tradeId, accountId FROM trades WHERE market = ? AND isLong = 0 AND stopLoss < ?");

			takeProfitTradesLong = tradeDB.prepareStatement("SELECT tradeId, accountId FROM trades WHERE market = ? AND isLong = 1 AND takeProfit < ?");
			takeProfitTradesShort = tradeDB.prepareStatement("SELECT tradeId, accountId FROM trades WHERE market = ? AND isLong = 0 AND takeProfit > ?");

			tradeClose = tradeDB.prepareStatement("DELETE FROM trades WHERE tradeId = ?");
			tradeCount = tradeDB.prepareStatement("SELECT count(*) FROM trades");
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
			logger.debug("FXTradeManager: Finished init() in " + (System.currentTimeMillis() - timestamp));
		}
	}

	public void preTickProcess() {
		if (!refreshPairs.isEmpty()) {
			for (final FXPair pair : refreshPairs) {
				refreshOrderMonitor(pair.getPair());
			}
			refreshPairs.clear();
		}
	}

	public void postTickProcess() {
		if (orderMonitors.isEmpty()) {
			return;
		}
		if (!marketManager.newTicksThisLoop()) {
			return;
		}
		final Multimap<FXPair, FXTick> newTicks = marketManager.getPerLoopTickTable();
		final Set<FXPair> markets = newTicks.keySet();
		// For each market with new ticks
		for (final FXPair pair : markets) {
			final String market = pair.getPair();
			final Collection<FXTick> ticks = newTicks.get(new FXPair(market));
			if (ticks.isEmpty()) {
				continue;
			}

			final OrderMonitor orderMonitor = orderMonitors.get(market);
			if (orderMonitor == null) {
				continue;
			}
			for (final FXTick tick : ticks) {
				if (orderMonitor.longStopLoss != 0 && orderMonitor.longStopLoss > tick.getBid()) {
					executeStopLosses(market, true, tick.getBid());
				}
				if (orderMonitor.longTakeProfit != 0 && orderMonitor.longTakeProfit < tick.getBid()) {
					executeTakeProfits(market, true, tick.getBid());
				}
				if (orderMonitor.shortStopLoss != 0 && orderMonitor.shortStopLoss < tick.getAsk()) {
					executeStopLosses(market, false, tick.getAsk());
				}
				if (orderMonitor.shortTakeProfit != 0 && orderMonitor.shortTakeProfit > tick.getAsk()) {
					executeTakeProfits(market, false, tick.getAsk());
				}
			}
		}

		// Todo: Check for Limit Orders achieved
		// Todo: Check for Limit Orders expired
	}

	public void close() {
		//Nothing to do. 
	}

	private void executeTakeProfits(String market, boolean isLong, double price) {
		if (logger.isDebugEnabled()) {
			logger.debug("FXTradeManager: Executing " + (isLong ? "long" : "short") + " take profits in " + market + " at " + price);
		}
		try {
			final ResultSet rs;
			if (isLong) {
				takeProfitTradesLong.clearParameters();
				takeProfitTradesLong.setString(1, market);
				takeProfitTradesLong.setDouble(2, price);
				rs = takeProfitTradesLong.executeQuery();
			} else {
				takeProfitTradesShort.clearParameters();
				takeProfitTradesShort.setString(1, market);
				takeProfitTradesShort.setDouble(2, price);
				rs = takeProfitTradesShort.executeQuery();
			}

			ListMultimap<Integer, MiniTradeResult> tradesToClose = new ArrayListMultimap<Integer, MiniTradeResult>();
			while (rs.next()) {
				MiniTradeResult tradeToClose = new MiniTradeResult();
				tradeToClose.transactionNumber = rs.getInt("tradeId");
				tradeToClose.accountNumber = rs.getInt("accountId");
				tradesToClose.put(tradeToClose.accountNumber, tradeToClose);
			}
			MarketOrder mo = new MarketOrder();
			final Set<Integer> tradesToCloseKeys = tradesToClose.keySet();
			for (final int accountNumber : tradesToCloseKeys) {
				final Account account = accountManager.getAccountWithId(accountNumber);
				final List<MiniTradeResult> trades = tradesToClose.get(accountNumber);
				for (final MiniTradeResult trade : trades) {
					UtilAPI.setTransactionNumber(trade.transactionNumber, mo);
					account.close(mo);
				}
			}
			refreshOrderMonitor(market);
		}
		catch (SQLException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		} catch (OAException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		}
	}

	private void executeStopLosses(final String market, final boolean isLong, final double price) {
		if (logger.isDebugEnabled()) {
			logger.debug("FXTradeManager: Executing " + (isLong ? "long" : "short") + " stop losses in " + market + " at " + price);
		}
		try {
			ResultSet rs;
			if (isLong) {
				stopLossTradesLong.clearParameters();
				stopLossTradesLong.setString(1, market);
				stopLossTradesLong.setDouble(2, price);
				rs = stopLossTradesLong.executeQuery();
			} else {
				stopLossTradesShort.clearParameters();
				stopLossTradesShort.setString(1, market);
				stopLossTradesShort.setDouble(2, price);
				rs = stopLossTradesShort.executeQuery();
			}

			ListMultimap<Integer, MiniTradeResult> tradesToClose = new ArrayListMultimap<Integer, MiniTradeResult>();
			while (rs.next()) {
				MiniTradeResult tradeToClose = new MiniTradeResult();
				tradeToClose.transactionNumber = rs.getInt("tradeId");
				tradeToClose.accountNumber = rs.getInt("accountId");
				tradesToClose.put(tradeToClose.accountNumber, tradeToClose);
			}
			MarketOrder mo = new MarketOrder();
			final Set<Integer> tradesToCloseKeys = tradesToClose.keySet();
			for (final int accountNumber : tradesToCloseKeys) {
				final Account account = accountManager.getAccountWithId(accountNumber);
				final List<MiniTradeResult> trades = tradesToClose.get(accountNumber);
				for (final MiniTradeResult trade : trades) {
					UtilAPI.setTransactionNumber(trade.transactionNumber, mo);
					account.close(mo);
				}
			}
			refreshOrderMonitor(market);
		}
		catch (SQLException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		} catch (OAException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		}

	}

	public void executeTrade(final MarketOrder trade, final Account account) {
		try {
			//INSERT INTO trades (tradeId, accountId, market, direction, stopLoss, takeProfit)
			final boolean isLong = trade.getUnits() >= 0;
			final double stopLoss = trade.getStopLoss() == null ? 0 : trade.getStopLoss().getPrice();
			final double takeProfit = trade.getTakeProfit() == null ? 0 : trade.getTakeProfit().getPrice();
			final String pair = trade.getPair().getPair();
			tradeInsert.clearParameters();
			tradeInsert.setInt(1, trade.getTransactionNumber());
			tradeInsert.setInt(2, account.getAccountId());
			tradeInsert.setString(3, pair);
			tradeInsert.setBoolean(4, isLong);
			tradeInsert.setDouble(5, stopLoss);
			tradeInsert.setDouble(6, takeProfit);
			tradeInsert.execute();
			updateOrderMonitor(pair, stopLoss, takeProfit, isLong);
			if (logger.isDebugEnabled()) {
				final ResultSet rs = tradeCount.executeQuery();
				rs.next();
				final int numberOfRows = rs.getInt(1);
				if (numberOfRows > 0) {
					logger.debug("FXTradeManager:  " + numberOfRows + " rows currently in db");
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		}
	}

	public void modifyTrade(final MarketOrder trade) {
		try {
			// UPDATE trades SET stopLoss = ?, takeProfit = ? WHERE tradeId = ?
			final double stopLoss = trade.getStopLoss() == null ? 0 : trade.getStopLoss().getPrice();
			final double takeProfit = trade.getTakeProfit() == null ? 0 : trade.getTakeProfit().getPrice();
			tradeUpdate.clearParameters();
			tradeUpdate.setDouble(1, stopLoss);
			tradeUpdate.setDouble(2, takeProfit);
			tradeUpdate.setInt(3, trade.getTransactionNumber());
			tradeUpdate.execute();

			refreshPairs.add(trade.getPair());
		}
		catch (SQLException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		}
	}

	public void closeTrade(final MarketOrder trade) {
		try {
			// DELETE FROM trades WHERE tradeId = ?
			final double stopLoss = trade.getStopLoss() == null ? 0 : trade.getStopLoss().getPrice();
			final double takeProfit = trade.getTakeProfit() == null ? 0 : trade.getTakeProfit().getPrice();
			tradeClose.clearParameters();
			tradeClose.setInt(1, trade.getTransactionNumber());
			tradeClose.execute();

			if (stopLoss != 0 || takeProfit != 0) {
				refreshPairs.add(trade.getPair());
			}
		}
		catch (SQLException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		}
	}

	private void updateOrderMonitor(final String market, final double stopLoss, final double takeProfit, final boolean isLong) {
		if (stopLoss == 0 && takeProfit == 0) {
			return;
		}
		OrderMonitor orderMonitor = null;
		// First trade in this market?
		if (!orderMonitors.containsKey(market)) {
			orderMonitor = new OrderMonitor(market);
			orderMonitors.put(market, orderMonitor);
		}
		if (orderMonitor == null) {
			orderMonitor = orderMonitors.get(market);
		}
		if (isLong) {
			// Long trades:  the highest stop loss and the lowest take profit
			orderMonitor.longStopLoss = Math.max(orderMonitor.longStopLoss, stopLoss);
			if (orderMonitor.longTakeProfit == 0) {
				orderMonitor.longTakeProfit = takeProfit;
			} else {
				orderMonitor.longTakeProfit = Math.min(orderMonitor.longTakeProfit, takeProfit);
			}
		} else {
			// Short trades:  the lowest stop loss and the highest take profit
			if (orderMonitor.shortStopLoss == 0) {
				orderMonitor.shortStopLoss = stopLoss;
			} else {
				orderMonitor.shortStopLoss = Math.min(orderMonitor.shortStopLoss, stopLoss);
			}
			orderMonitor.shortTakeProfit = Math.max(orderMonitor.shortTakeProfit, takeProfit);
		}
	}

	private void refreshOrderMonitor(final String pair) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("FXTradeManager: Refreshing order monitor for " + pair);
			}

			ResultSet rs;
			tradeSelectLongStopLoss.clearParameters();
			tradeSelectLongStopLoss.setString(1, pair);
			rs = tradeSelectLongStopLoss.executeQuery();
			final double longStopLoss;
			if (rs.next()) {
				longStopLoss = rs.getDouble(1);
			} else {
				longStopLoss = 0;
			}

			tradeSelectLongTakeProfit.clearParameters();
			tradeSelectLongTakeProfit.setString(1, pair);
			rs = tradeSelectLongTakeProfit.executeQuery();
			final double longTakeProfit;
			if (rs.next()) {
				longTakeProfit = rs.getDouble(1);
			} else {
				longTakeProfit = 0;
			}

			tradeSelectShortStopLoss.clearParameters();
			tradeSelectShortStopLoss.setString(1, pair);
			rs = tradeSelectShortStopLoss.executeQuery();
			final double shortStopLoss;
			if (rs.next()) {
				shortStopLoss = rs.getDouble(1);
			} else {
				shortStopLoss = 0;
			}

			tradeSelectShortTakeProfit.clearParameters();
			tradeSelectShortTakeProfit.setString(1, pair);
			rs = tradeSelectShortTakeProfit.executeQuery();
			final double shortTakeProfit;
			if (rs.next()) {
				shortTakeProfit = rs.getDouble(1);
			} else {
				shortTakeProfit = 0;
			}

			if (longTakeProfit == 0 && longStopLoss == 0 && shortTakeProfit == 0 && shortStopLoss == 0) {
				orderMonitors.remove(pair);
				return;
			}
			OrderMonitor orderMonitor = orderMonitors.get(pair);
			if (orderMonitor == null) {
				orderMonitor = new OrderMonitor(pair);
				orderMonitors.put(pair, orderMonitor);
			}
			orderMonitor.longStopLoss = longStopLoss;
			orderMonitor.longTakeProfit = longTakeProfit;
			orderMonitor.shortStopLoss = shortStopLoss;
			orderMonitor.shortTakeProfit = shortTakeProfit;
			if (logger.isDebugEnabled()) {
				logger.debug(orderMonitor);
			}
		}
		catch (SQLException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		}
	}

	public void setAccountManager(final IAccountManager accountManager) {
		this.accountManager = (FXAccountManager) accountManager;
	}

	private class MiniTradeResult {
		int transactionNumber;
		int accountNumber;
	}

	private class OrderMonitor {
		private OrderMonitor(final String market) {
			this.market = market;
		}

		public String getMarket() {
			return market;
		}

		private String market;
		double longStopLoss;
		double shortStopLoss;
		double longTakeProfit;
		double shortTakeProfit;

		public String toString() {
			return "OrderMonitor [market=" + market + ", " +
					"longStopLoss=" + longStopLoss + ", " +
					"shortStopLoss=" + shortStopLoss + ", " +
					"longTakeProfit=" + longTakeProfit + ", " +
					"shortTakeProfit=" + shortTakeProfit + "]";
		}
	}

}
