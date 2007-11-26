package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.Engine;
import com.dojoconsulting.gigawatt.core.NotImplementedException;
import com.dojoconsulting.gigawatt.core.fximpl.FXTradeManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An Account object represents an existing Oanda account.  Accounts cannot be created through this API.  Accounts are
 * identified by a unique integer accountId (accountId).  Current open trades, orders and transactions are maintained and kept
 * up-to-date within the object.
 */
public final class Account {

	private Log logger = LogFactory.getLog(FXTradeManager.class);

	private int accountId;
	private double balance;
	private String homeCurrency;
	private String accountName;
	private long createDate;

	private Map<FXPair, Position> positions;
	private List<LimitOrder> orders;
	private List<MarketOrder> trades;
	private List<Transaction> transactions;
	private List<MarketOrder> closedTrades;

	private double realizedPL;
	private int leverage;
	private Engine engine;
	private FXTradeManager tradeManager;

	private FXEventManager eventManager = new FXEventManager() {
		@SuppressWarnings("unchecked")
		final void event(final FXAccountEventInfo ei) {
			final List<FXEvent> events = (List<FXEvent>) getEvents();
			for (final FXEvent event : events) {
				if (event instanceof FXAccountEvent) {
					final FXAccountEvent accountEvent = (FXAccountEvent) event;
					if (accountEvent.match(ei)) {
						event.handle(ei, this);
					}
				}
			}
		}
	};

	void setEngine(final Engine engine) {
		this.engine = engine;
		tradeManager = (FXTradeManager) engine.getTradeManager();
	}

	Account(final int accountId, final double balance, final String homeCurrency, final String accountName, final long createDate, final int leverage) {
		this.createDate = createDate;
		this.accountId = accountId;
		this.balance = balance;
		this.homeCurrency = homeCurrency;
		this.accountName = accountName;
		this.leverage = leverage;

		positions = new HashMap<FXPair, Position>();
		orders = new ArrayList<LimitOrder>();
		trades = new ArrayList<MarketOrder>();
		closedTrades = new ArrayList<MarketOrder>();
		transactions = new ArrayList<Transaction>();

		realizedPL = 0.0;
	}

	public void close(final MarketOrder mo) throws OAException {
		final int transactionNumber = mo.getTransactionNumber();
		final MarketOrder closeTrade = internalGetTradeWithId(transactionNumber);
		if (closeTrade == null) {
			throw new OAException("Invalid MarketOrder " + transactionNumber);
		}
		logger.debug("Account: Closing trade " + transactionNumber + " in " + closeTrade.getPair().getPair());
		closeTrade(closeTrade);
		final Position position = positions.get(closeTrade.getPair());
		position.closeTrade(closeTrade);
		if (position.getTrades().size() == 0) {
			position.close();
			positions.remove(closeTrade.getPair());
		}
		trades.remove(closeTrade);
	}

	private void closeTrade(final MarketOrder closeTrade) {
		final Map tickTable = engine.getMarketManager().getTickTable();
		final FXTick tick = (FXTick) tickTable.get(closeTrade.getPair());
		final MarketOrder close = new MarketOrder();
		if (closeTrade.isLong()) {
			close.setPrice(tick.getBid());
		} else {
			close.setPrice(tick.getAsk());
		}
		UtilAPI.closeTrade(closeTrade, close);

		// balance adjustment
		final double profitInQuoteCurrency = closeTrade.getRealizedPL();
		final FXPair pair = closeTrade.getPair();
		final double profitInHomeCurrency;
		if (pair.getQuote().equals(getHomeCurrency())) {
			profitInHomeCurrency = profitInQuoteCurrency;
		} else {
			final FXTick convert = UtilMath.getConverstionRate(pair.getQuote(), getHomeCurrency(), tickTable);
			profitInHomeCurrency = profitInQuoteCurrency * convert.getMean();
		}
		balance += profitInHomeCurrency;
		realizedPL += profitInHomeCurrency;

		tradeManager.closeTrade(closeTrade);
		closedTrades.add(closeTrade);
		// todo: add transaction
		// todo: interest calc
	}

	public void close(final String position) throws OAException {
		final FXPair pair = new FXPair(position);
		final Position thePosition = positions.get(pair);
		if (thePosition == null) {
			throw new OAException("Invalid position pair " + position);
		}
		final List<MarketOrder> positionTrades = thePosition.getTrades();
		final int size = positionTrades.size();
		for (int i = 0; i < size; i++) {
			final MarketOrder closeTrade = positionTrades.get(i);
			closeTrade(closeTrade);
			trades.remove(closeTrade);
		}
		thePosition.close();
		positions.remove(pair);
	}

	public void modify(final MarketOrder mo) throws OAException {
		MarketOrder modifyTrade = null;
		final long transactionNumber = mo.getTransactionNumber();
		final int size = trades.size();
		for (int i = 0; i < size; i++) {
			final MarketOrder trade = trades.get(i);
			if (trade.getTransactionNumber() == transactionNumber) {
				modifyTrade = trade;
				break;
			}
		}
		if (modifyTrade == null) {
			throw new OAException("Invalid MarketOrder " + transactionNumber);
		}
		mo.setPrice(modifyTrade.getPrice());
		mo.validateOrders();
		tradeManager.modifyTrade(mo);
		modifyTrade.setStopLoss(mo.getStopLoss());
		modifyTrade.setTakeProfit(mo.getTakeProfit());
	}

	public void modify(final LimitOrder lo) throws OAException {
		//todo: Implement modify(LimitOrder)
	}

	public void execute(final LimitOrder lo) throws OAException {
		//todo: Implement execute(LimitOrder)

	}

	public void close(final LimitOrder lo) throws OAException {
		//todo: Implement close(LimitOrder)

	}

	public void execute(final MarketOrder mo) throws OAException {
		final Map tickTable = engine.getMarketManager().getTickTable();

		final FXPair pair = mo.getPair();
		final FXTick tick = (FXTick) tickTable.get(pair);
		mo.validate(tick);

		final int transactionNumber = FXTradeManager.getNextTicketNumber();
		mo.setTransactionNumber(transactionNumber);

		tradeManager.executeTrade(mo, this);
		final MarketOrder newOrder = (MarketOrder) mo.clone();
		trades.add(newOrder);

		Position position = positions.get(pair);
		if (position == null) {
			position = new Position(pair);
			positions.put(pair, position);
		}
		position.addMarketOrder(newOrder);

		//todo: Implement execute(MarketOrder) properly (still needs to check for reversal)
		//todo: Needs to check for sufficient funds.
	}

	public int getAccountId() {
		return accountId;
	}

	public double getBalance() throws AccountException {
		return balance;
	}

	public String getHomeCurrency() {
		return homeCurrency;
	}

	public String getAccountName() {
		return accountName;
	}

	public List getPositions() throws AccountException {
		return new ArrayList<Position>(positions.values());
	}

	@SuppressWarnings("unchecked")
	public List<LimitOrder> getOrders() throws AccountException {
		return (List<LimitOrder>) ((ArrayList) orders).clone();
	}

	public Position getPosition(final FXPair pair) throws AccountException {
		return positions.get(pair);
	}

	public double getPositionValue() throws AccountException {
		final Map tickTable = engine.getMarketManager().getTickTable();

		double totalPositionValue = 0;
		final int size = trades.size();
		for (int i = 0; i < size; i++) {
			final MarketOrder mo = trades.get(i);
			final long units = mo.getUnits();
			final FXPair pair = mo.getPair();

			totalPositionValue += UtilMath.calculatePositionValue(pair, units, homeCurrency, tickTable);
		}

		return totalPositionValue;
	}

	public double getUnrealizedPL() throws AccountException {
		final Map tickTable = engine.getMarketManager().getTickTable();
		int value = 0;

		final Collection<Position> positionValues = positions.values();
		for (final Position position : positionValues) {
			final FXTick tick = (FXTick) tickTable.get(position.getPair());
			final FXPair pair = position.getPair();
			double profit = position.getUnrealizedPL(tick);
			if (!pair.getQuote().equals(getHomeCurrency())) {
				final FXTick convert = UtilMath.getConverstionRate(pair.getQuote(), getHomeCurrency(), tickTable);
				profit = profit * convert.getMean();
			}
			value += profit;
		}
		return value;
	}

	public LimitOrder getOrderWithId(final int transactionNumber) throws AccountException {
		for (final LimitOrder order : orders) {
			if (order.getTransactionNumber() == transactionNumber) {
				return (LimitOrder) order.clone();
			}
		}
		return null;
	}

	private MarketOrder internalGetTradeWithId(final int transactionNumber) {
		for (final MarketOrder trade : trades) {
			if (trade.getTransactionNumber() == transactionNumber) {
				return trade;
			}
		}
		return null;
	}

	public MarketOrder getTradeWithId(final int transactionNumber) throws AccountException {
		// todo: Should this return null or throw AccountException if not found
		final MarketOrder order = internalGetTradeWithId(transactionNumber);
		if (order != null) {
			return (MarketOrder) order.clone();
		}
		return null;
	}

	public Transaction getTransactionWithId(final int transactionNumber) throws AccountException {
		for (final Transaction transaction : transactions) {
			if (transaction.getTransationNumber() == transactionNumber) {
				return transaction;
			}
		}
		return null;
	}


	public long getCreateDate() {
		return createDate;
	}

	public double getMarginAvailable() throws AccountException {
		final double netAssetValue = getNetAssetValue();
		final double marginUsed = getMarginUsed();
		final double result = netAssetValue - marginUsed;
		if (result < 0) {
			return 0.0;
		}
		return result;
	}

	public double getMarginCallRate() throws AccountException {
		final double marginUsed = getMarginUsed();
		if (marginUsed == 0) {
			return 0;
		}
		return getMarginUsed() / 2;
	}

	public double getMarginRate() {
		//todo: Implement getMarginRate()
		// Possibly the inverse of Leverage?
		return 1 / leverage;
	}

	public double getMarginUsed() throws AccountException {
		final Map tickTable = engine.getMarketManager().getTickTable();

		double totalMarginUsed = 0;
		final int size = trades.size();
		for (int i = 0; i < size; i++) {
			final MarketOrder mo = trades.get(i);
			final long units = mo.getUnits();
			final FXPair pair = mo.getPair();

			final double positionValue = UtilMath.calculatePositionValue(pair, units, homeCurrency, tickTable);
			final double marginRequirement = UtilMath.marginPercentageRequired(pair, leverage);
			totalMarginUsed += (positionValue * marginRequirement);
		}

		return totalMarginUsed;
	}

	public String getProfile() {
		throw new NotImplementedException("account.getProfile() is not supported in this version of the FXOandaBackTest tool.");
	}

	public void setProfile(final String newprofile) throws AccountException {
		throw new NotImplementedException("account.setProfile() is not supported in this version of the FXOandaBackTest tool: " + newprofile);
	}

	public double getRealizedPL() throws AccountException {
		return realizedPL;
	}

	@SuppressWarnings("unchecked")
	public List<MarketOrder> getTrades() throws AccountException {
		return (List<MarketOrder>) ((ArrayList) trades).clone();
	}

	public List<Transaction> getTransactions() throws AccountException {
		return transactions;
	}

	public FXEventManager getEventManager() throws AccountException {
		return eventManager;
	}

	public String toString() {
		try {
			String result = "Account " + accountName + " (" + accountId + "): ";

			result += "Balance [" + UtilMath.round(balance, 2) + "]\t";
			result += "Trades [" + trades.size() + "]\t";
			result += "Orders [" + orders.size() + "]\t";
			result += "marginCall [" + UtilMath.round(getNetAssetValue(), 2) + " " + getMarginCallRate() + "]\t";
			result += "R P/L [" + UtilMath.round(realizedPL, 2) + "]";

			return result;
		} catch (AccountException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		//todoproper: Implement toString()
		return super.toString();
	}

	void process() {
		try {
			if (engine.getMarketManager().newTicksThisLoop() && trades.size() > 0) {
				logger.info("Currenct NAV/Balance: " + getNetAssetValue() + " " + getBalance());
				if (getNetAssetValue() <= getMarginCallRate()) {
					logger.info("Margin Call on Account: " + accountId);
					final int size = trades.size();
					for (int i = 0; i < size; i++) {
						final MarketOrder trade = trades.get(i);
						closeTrade(trade);
					}
					trades.clear();
					positions.clear();
				}
				if (getBalance() <= 0 ) {
					logger.info("Account Busted: " + accountId);
					final int size = trades.size();
					for (int i = 0; i < size; i++) {
						final MarketOrder trade = trades.get(i);
						closeTrade(trade);
					}
					trades.clear();
					positions.clear();
				}
			}
		} catch (AccountException e) {
			e.printStackTrace();  //todoerror: Improve error handling.
		}
	}

	double getNetAssetValue() throws AccountException {
		return getBalance() + getUnrealizedPL();
	}

	int getLeverage() {
		return leverage;
	}

}
