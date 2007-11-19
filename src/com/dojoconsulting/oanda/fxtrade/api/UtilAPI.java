package com.dojoconsulting.oanda.fxtrade.api;

import java.util.Vector;

/**
 * A file of horrid hacks to keep the API interface matched to OANDA.
 */
public class UtilAPI {

    public static void setUserAccounts(final User user, final Vector<Account> accounts) {
        user.setAccounts(accounts);
    }

    public static User createUser(final int userId, final String userName, final String password, final String name, final String address, final String telephone, final String email, final long createDate) {
        return new User(userId, userName, password, name, address, telephone, email, createDate);
    }

    public static Account createAccount(final int accountId, final double balance, final String homeCurrency, final String accountName, final long createDate, final int leverage) {
        return new Account(accountId, balance, homeCurrency, accountName, createDate, leverage);
    }

    public static void processAccount(final Account account)  {
        account.process();
    }

    public static void setTransactionNumber(int transactionNumber, MarketOrder trade) {
        trade.setTransactionNumber(transactionNumber);
    }

    public static void closeTrade(MarketOrder closingTrade, MarketOrder closePrice) {
        closingTrade.setClose(closePrice);
        //To change body of created methods use File | Settings | File Templates.
    }
}
