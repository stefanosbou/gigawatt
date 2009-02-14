package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.Engine;
import com.dojoconsulting.gigawatt.core.fximpl.accountprocessor.AccountProcessorFactory;
import com.dojoconsulting.gigawatt.core.fximpl.domain.TransactionType;

import java.util.List;

/**
 * A file of horrid hacks to keep the API interface matched to OANDA.
 */
public class UtilAPI {

	public static void setUserAccounts(final User user, final List<Account> accounts) {
		user.setAccounts(accounts);
	}

	public static User createUser(final int userId, final String userName, final String password, final String name, final String address, final String telephone, final String email, final long createDate) {
		return new User(userId, userName, password, name, address, telephone, email, createDate);
	}

	public static Account createAccount(final int accountId, final double balance, final String homeCurrency, final String accountName, final long createDate, final int leverage, final String processType, final Engine engine) {
		final Account a = new Account(accountId, balance, homeCurrency, accountName, createDate, leverage);
		a.setProcessor(AccountProcessorFactory.getProcessor(processType));
		a.setEngine(engine);
		return a;
	}

	public static Transaction createTransaction(final double amount, final double balance, final int completionCode, final int diaspora, final double interest, final double margin, final FXPair pair, final double price, final double stopLoss, final double takeProfit, final long timestamp, final int transactionLink, final int transactionNumber, final long units, final String type, final long expiry, final double lowerBound, final double upperBound, final double profitLoss) {
		return new Transaction(amount, balance, completionCode, diaspora, interest, margin, pair, price, stopLoss, takeProfit, timestamp, transactionLink, transactionNumber, units, type, expiry, lowerBound, upperBound, profitLoss);
	}

	public static void processAccount(final Account account) {
		account.process();
	}

	public static void setTransactionNumber(final int transactionNumber, final MarketOrder trade) {
		trade.setTransactionNumber(transactionNumber);
	}

	public static void closeTrade(final MarketOrder closingTrade, final MarketOrder closePrice) {
		closingTrade.setClose(closePrice);
	}

	public static FXHistoryPoint createNewHistoryPointFromOld(final FXHistoryPoint oldPoint, final long interval) {
		return oldPoint.createNextPoint(interval);
	}

	public static void updateFXHistoryPoint(final FXHistoryPoint currentPoint, final FXTick tick) {
		currentPoint.updatePoint(tick);
	}

	public static FXHistoryPoint createStartingFXHistoryPoint(final long start, final FXTick tick) {
		return new FXHistoryPoint(start, tick);
	}

	public static void closeTrade(final Account account, final MarketOrder mo, final TransactionType transactionType) throws OAException {
		account.close(mo, transactionType);
	}

	public static void setTransactionNumber(final int transactionNumber, final LimitOrder limitOrder) {
		limitOrder.setTransactionNumber(transactionNumber);
	}

	public static boolean validateOrderPurchase(final Account account, final LimitOrder limitOrder) throws AccountException {
		return account.validatePurchase(limitOrder);
	}

	public static void executeOrder(final Account account, final LimitOrder lo, final TransactionType transactionType) throws OAException {
		account.executeOrder(lo, transactionType);
	}

	public static void setOrderClosed(final LimitOrder lo, final Account account) {
		account.orderClosed(lo);
	}
}
