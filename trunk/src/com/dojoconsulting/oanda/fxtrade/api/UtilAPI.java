package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.Engine;

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

	public static Account createAccount(final int accountId, final double balance, final String homeCurrency, final String accountName, final long createDate, final int leverage, final Engine engine) {
		final Account a = new Account(accountId, balance, homeCurrency, accountName, createDate, leverage);
		a.setEngine(engine);
		return a;
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
}
