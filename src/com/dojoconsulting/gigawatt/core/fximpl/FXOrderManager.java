package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.IAccountManager;
import com.dojoconsulting.gigawatt.core.IMarketManager;
import com.dojoconsulting.gigawatt.core.IOrderManager;
import com.dojoconsulting.gigawatt.core.fximpl.domain.TransactionType;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;
import com.dojoconsulting.oanda.fxtrade.api.LimitOrder;
import com.dojoconsulting.oanda.fxtrade.api.OAException;
import com.dojoconsulting.oanda.fxtrade.api.UtilAPI;
import com.dojoconsulting.oanda.fxtrade.api.UtilMath;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
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
 * Date: 11-Dec-2007
 * Time: 23:42:01
 */

public class FXOrderManager implements IOrderManager {

	private Log logger = LogFactory.getLog(FXOrderManager.class);

	private PreparedStatement orderInsert;
	private PreparedStatement orderUpdate;
	private PreparedStatement orderClose;

	private PreparedStatement ordersMaxBelowPrice;
	private PreparedStatement ordersMinAbovePrice;

	private PreparedStatement ordersAbovePrice;
	private PreparedStatement ordersBelowPrice;

	private PreparedStatement orderCount;
	private Map<String, LimitMonitor> limitMonitors;

	private Set<FXPair> refreshPairs;

	private DataSource dataSource;

	private IMarketManager marketManager;
	private IAccountManager accountManager;


	public void setMarketManager(final IMarketManager marketManager) {
		this.marketManager = marketManager;
	}

	public void setAccountManager(final IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}


	public void init(final BackTestConfig config) {
		long timestamp = 0;
		if (logger.isDebugEnabled()) {
			timestamp = System.currentTimeMillis();
			logger.debug("FXOrderManager: Started init()");
		}
		try {
			limitMonitors = new HashMap<String, LimitMonitor>();
			refreshPairs = new HashSet<FXPair>();

			final Connection orderDB = dataSource.getConnection();

			final Statement st = orderDB.createStatement();
			final String expression = "CREATE TABLE orders ( orderId INTEGER IDENTITY, accountId INTEGER, market CHAR(7), isLong BOOLEAN, isAbovePrice BOOLEAN, price FLOAT, stopLoss FLOAT, takeProfit FLOAT, expiry BIGINT)";
			st.executeUpdate(expression);
			st.close();

			orderInsert = orderDB.prepareStatement("INSERT INTO orders (orderId, accountId, market, isLong, isAbovePrice, price, stopLoss, takeProfit, expiry) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			orderUpdate = orderDB.prepareStatement("UPDATE orders SET stopLoss = ?, takeProfit = ?, price = ?, expiry = ? WHERE orderId = ?");

			ordersMaxBelowPrice = orderDB.prepareStatement("SELECT max(price) FROM orders WHERE isAbovePrice=0 AND market = ? AND isLong = ?");
			ordersMinAbovePrice = orderDB.prepareStatement("SELECT min(price) FROM orders WHERE isAbovePrice=1 AND market = ? AND isLong = ?");

			ordersAbovePrice = orderDB.prepareStatement("SELECT orderId, accountId, takeProfit, stopLoss FROM orders WHERE market = ? AND isLong = ? AND price <= ?");
			ordersBelowPrice = orderDB.prepareStatement("SELECT orderId, accountId, takeProfit, stopLoss FROM orders WHERE market = ? AND isLong = ? AND price >= ?");

			orderClose = orderDB.prepareStatement("DELETE FROM orders WHERE orderId = ?");
			orderCount = orderDB.prepareStatement("SELECT count(*) FROM orders");
		}
		catch (SQLException e) {
			e.printStackTrace();  // Todoerror: Improve error handling
			System.exit(1);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("FXOrderManager: Finished init() in " + (System.currentTimeMillis() - timestamp));
		}
	}

	public void preTickProcess() {
		if (!refreshPairs.isEmpty()) {
			for (final FXPair pair : refreshPairs) {
				refreshLimitMonitor(pair.getPair());
			}
			refreshPairs.clear();
		}
	}

	private void refreshLimitMonitor(final String pair) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("FXOrderManager: Refreshing limit monitor for " + pair);
			}

			ResultSet rs;
			// Long Above Price
			ordersMinAbovePrice.clearParameters();
			ordersMinAbovePrice.setString(1, pair);
			ordersMinAbovePrice.setBoolean(2, true);
			rs = ordersMinAbovePrice.executeQuery();
			final double longAbovePrice;
			if (rs.next()) {
				longAbovePrice = rs.getDouble(1);
			} else {
				longAbovePrice = 0;
			}

			// Short Above Price
			ordersMinAbovePrice.clearParameters();
			ordersMinAbovePrice.setString(1, pair);
			ordersMinAbovePrice.setBoolean(2, false);
			rs = ordersMinAbovePrice.executeQuery();
			final double shortAbovePrice;
			if (rs.next()) {
				shortAbovePrice = rs.getDouble(1);
			} else {
				shortAbovePrice = 0;
			}

			// Long Below Price
			ordersMaxBelowPrice.clearParameters();
			ordersMaxBelowPrice.setString(1, pair);
			ordersMaxBelowPrice.setBoolean(2, true);
			rs = ordersMaxBelowPrice.executeQuery();
			final double longBelowPrice;
			if (rs.next()) {
				longBelowPrice = rs.getDouble(1);
			} else {
				longBelowPrice = 0;
			}

			// Short Below Price
			ordersMaxBelowPrice.clearParameters();
			ordersMaxBelowPrice.setString(1, pair);
			ordersMaxBelowPrice.setBoolean(2, false);
			rs = ordersMaxBelowPrice.executeQuery();
			final double shortBelowPrice;
			if (rs.next()) {
				shortBelowPrice = rs.getDouble(1);
			} else {
				shortBelowPrice = 0;
			}

			LimitMonitor limitMonitor = limitMonitors.get(pair);
			if (longAbovePrice == 0 && longBelowPrice == 0 && shortAbovePrice == 0 && shortBelowPrice == 0) {
				if (limitMonitor == null) {
					return;
				}
				limitMonitor.longAbovePrice = 0;
				limitMonitor.longBelowPrice = 0;
				limitMonitor.shortAbovePrice = 0;
				limitMonitor.shortBelowPrice = 0;
				limitMonitors.remove(pair);
				return;
			}
			if (limitMonitor == null) {
				limitMonitor = new LimitMonitor(pair);
				limitMonitors.put(pair, limitMonitor);
			}
			limitMonitor.longAbovePrice = longAbovePrice;
			limitMonitor.longBelowPrice = longBelowPrice;
			limitMonitor.shortAbovePrice = shortAbovePrice;
			limitMonitor.shortBelowPrice = shortBelowPrice;
			if (logger.isDebugEnabled()) {
				logger.debug(limitMonitor);
			}
		}
		catch (SQLException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		}

	}

	public void postTickProcess() {
		if (limitMonitors.isEmpty()) {
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

			final LimitMonitor limitMonitor = limitMonitors.get(market);
			if (limitMonitor == null) {
				continue;
			}
			for (final FXTick tick : ticks) {
				if (limitMonitor.longAbovePrice != 0 && limitMonitor.longAbovePrice <= tick.getAsk()) {
					executeOrderTrade(market, true, true, tick.getAsk(), tick.getBid());
				}
				if (limitMonitor.longBelowPrice != 0 && limitMonitor.longBelowPrice >= tick.getAsk()) {
					executeOrderTrade(market, false, true, tick.getAsk(), tick.getBid());
				}
				if (limitMonitor.shortAbovePrice != 0 && limitMonitor.shortAbovePrice <= tick.getBid()) {
					executeOrderTrade(market, true, false, tick.getBid(), tick.getAsk());
				}
				if (limitMonitor.shortBelowPrice != 0 && limitMonitor.shortBelowPrice >= tick.getBid()) {
					executeOrderTrade(market, false, false, tick.getBid(), tick.getAsk());
				}
			}
		}
	}

	private void executeOrderTrade(final String market, final boolean isAbovePrice, final boolean isLong, final double currentPrice, final double currentPriceForClose) {
		if (logger.isDebugEnabled()) {
			logger.debug("FXOrderManager: Executing " + (isLong ? "long" : "short") + " limit orders " + (isAbovePrice ? "above" : "below") + " price in " + market + " at current price of " + currentPrice);
		}
		try {
			final ResultSet rs;
			if (isAbovePrice) {
				ordersAbovePrice.clearParameters();
				ordersAbovePrice.setString(1, market);
				ordersAbovePrice.setBoolean(2, isLong);
				ordersAbovePrice.setDouble(3, currentPrice);
				rs = ordersAbovePrice.executeQuery();
			} else {
				ordersBelowPrice.clearParameters();
				ordersBelowPrice.setString(1, market);
				ordersBelowPrice.setBoolean(2, isLong);
				ordersBelowPrice.setDouble(3, currentPrice);
				rs = ordersBelowPrice.executeQuery();
			}
			final ListMultimap<Integer, MiniOrderResult> ordersToExecute = new ArrayListMultimap<Integer, MiniOrderResult>();
			while (rs.next()) {
				final MiniOrderResult orderToExecute = new MiniOrderResult();
				orderToExecute.transactionNumber = rs.getInt("orderId");
				orderToExecute.accountNumber = rs.getInt("accountId");
				orderToExecute.takeProfit = rs.getDouble("takeProfit");
				orderToExecute.stopLoss = rs.getDouble("stopLoss");
				ordersToExecute.put(orderToExecute.accountNumber, orderToExecute);
			}
			final LimitOrder lo = new LimitOrder(0);
			final Set<Integer> ordersToExecuteKeys = ordersToExecute.keySet();
			for (final int accountNumber : ordersToExecuteKeys) {
				final Account account = accountManager.getAccountWithId(accountNumber);
				final List<MiniOrderResult> orderResults = ordersToExecute.get(accountNumber);
				for (final MiniOrderResult orderResult : orderResults) {

					UtilAPI.setTransactionNumber(orderResult.transactionNumber, lo);
					UtilAPI.setOrderClosed(lo, account);

					if (isLong) {
						if (orderResult.takeProfit != 0 && currentPriceForClose >= orderResult.takeProfit) {
							closeOrder(lo, TransactionType.ORDER_BSV);
							continue;
						}
						if (orderResult.stopLoss != 0 && currentPriceForClose <= orderResult.stopLoss) {
							closeOrder(lo, TransactionType.ORDER_BSV);
							continue;
						}
					} else {
						if (orderResult.takeProfit != 0 && currentPriceForClose <= orderResult.takeProfit) {
							closeOrder(lo, TransactionType.ORDER_BSV);
							continue;
						}
						if (orderResult.stopLoss != 0 && currentPriceForClose >= orderResult.stopLoss) {
							closeOrder(lo, TransactionType.ORDER_BSV);
							continue;
						}
					}
					final boolean canAffordPurchase = UtilAPI.validateOrderPurchase(account, lo);
					if (!canAffordPurchase) {
						closeOrder(lo, TransactionType.ORDER_NSF);
						continue;
					}
					UtilAPI.executeOrder(account, lo, TransactionType.ORDER_FILL);
					closeOrder(lo, TransactionType.ORDER_FILL);
				}
			}
			refreshLimitMonitor(market);
		}
		catch (SQLException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		} catch (OAException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		}
	}

	public void close() {
		//Nothing to do
	}

	public void executeOrder(final LimitOrder newOrder, final boolean isAbovePrice, final Account account) {
		try {
			//INSERT INTO orders (orderId, accountId, market, isLong, isAbovePrice, price, stopLoss, takeProfit)
			final boolean isLong = newOrder.getUnits() >= 0;
			final double stopLoss = newOrder.getStopLoss() == null ? 0 : newOrder.getStopLoss().getPrice();
			final double takeProfit = newOrder.getTakeProfit() == null ? 0 : newOrder.getTakeProfit().getPrice();
			final String pair = newOrder.getPair().getPair();
			orderInsert.clearParameters();
			orderInsert.setInt(1, newOrder.getTransactionNumber());
			orderInsert.setInt(2, account.getAccountId());
			orderInsert.setString(3, pair);
			orderInsert.setBoolean(4, isLong);
			orderInsert.setBoolean(5, isAbovePrice);
			orderInsert.setDouble(6, newOrder.getPrice());
			orderInsert.setDouble(7, stopLoss);
			orderInsert.setDouble(8, takeProfit);
			orderInsert.setLong(9, newOrder.getExpiry());
			orderInsert.execute();
			updateOrderMonitor(pair, newOrder.getPrice(), isLong, isAbovePrice);
			if (logger.isDebugEnabled()) {
				final ResultSet rs = orderCount.executeQuery();
				rs.next();
				final int numberOfRows = rs.getInt(1);
				if (numberOfRows > 0) {
					logger.debug("FXOrderManager:  " + numberOfRows + " rows currently in orders db");
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		}
	}

	public void closeOrder(final LimitOrder closeOrder, final TransactionType transactionType) {
		try {
			// DELETE FROM orders WHERE orderId = ?
			orderClose.clearParameters();
			orderClose.setInt(1, closeOrder.getTransactionNumber());
			orderClose.execute();

			if (transactionType == TransactionType.USER || transactionType == TransactionType.ORDER_EXPIRY) {
				refreshPairs.add(closeOrder.getPair());
			}
			if (logger.isDebugEnabled()) {
				final ResultSet rs = orderCount.executeQuery();
				rs.next();
				final int numberOfRows = rs.getInt(1);
				if (numberOfRows > 0) {
					logger.debug("FXOrderManager:  " + numberOfRows + " rows currently in orders db");
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		}
	}

	public void modifyOrder(final LimitOrder modifyOrder) {
		try {
			// UPDATE orders SET stopLoss = ?, takeProfit = ? WHERE tradeId = ?
			final double stopLoss = modifyOrder.getStopLoss() == null ? 0 : modifyOrder.getStopLoss().getPrice();
			final double takeProfit = modifyOrder.getTakeProfit() == null ? 0 : modifyOrder.getTakeProfit().getPrice();
			final double price = modifyOrder.getPrice();
			final long expiry = modifyOrder.getExpiry();
			orderUpdate.clearParameters();
			orderUpdate.setDouble(1, stopLoss);
			orderUpdate.setDouble(2, takeProfit);
			orderUpdate.setDouble(3, price);
			orderUpdate.setDouble(4, expiry);
			orderUpdate.setInt(5, modifyOrder.getTransactionNumber());
			orderUpdate.execute();

			refreshPairs.add(modifyOrder.getPair());
		}
		catch (SQLException e) {
			e.printStackTrace(); //TODO error:: Improve error handling.
			System.exit(1);
		}
	}

	private void updateOrderMonitor(final String market, final double price, final boolean isLong, final boolean isAbovePrice) {
		final LimitMonitor limitMonitor;
		// First trade in this market?
		if (!limitMonitors.containsKey(market)) {
			limitMonitor = new LimitMonitor(market);
			limitMonitors.put(market, limitMonitor);
		} else {
			limitMonitor = limitMonitors.get(market);
		}
		if (isLong) {
			// Orders:  the lowest abovePrice or the highest below price
			if (isAbovePrice) {
				if (limitMonitor.longAbovePrice == 0) {
					limitMonitor.longAbovePrice = price;
				} else {
					limitMonitor.longAbovePrice = Math.min(limitMonitor.longAbovePrice, price);
				}
			} else {
				limitMonitor.longBelowPrice = Math.max(limitMonitor.longBelowPrice, price);
			}
		} else {
			// Orders:  the lowest abovePrice or the highest below price
			if (isAbovePrice) {
				if (limitMonitor.shortAbovePrice == 0) {
					limitMonitor.shortAbovePrice = price;
				} else {
					limitMonitor.shortAbovePrice = Math.min(limitMonitor.shortAbovePrice, price);
				}
			} else {
				limitMonitor.shortBelowPrice = Math.max(limitMonitor.shortBelowPrice, price);
			}
		}
	}

	private class MiniOrderResult {
		int transactionNumber;
		int accountNumber;
		double takeProfit;
		double stopLoss;
	}

	private class LimitMonitor {
		private LimitMonitor(final String market) {
			this.market = market;
		}

		public String getMarket() {
			return market;
		}

		private String market;
		double longAbovePrice;
		double longBelowPrice;
		double shortAbovePrice;
		double shortBelowPrice;

		public String toString() {
			return "LimitMonitor [market=" + market + ", " +
					"longAbovePrice=" + UtilMath.round(longAbovePrice, 6) + ", " +
					"longBelowPrice=" + UtilMath.round(longBelowPrice, 6) + ", " +
					"shortAbovePrice=" + UtilMath.round(shortAbovePrice, 6) + ", " +
					"shortBelowPrice=" + UtilMath.round(shortBelowPrice, 6) + "]";
		}
	}

}
