package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.AccountConfig;
import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.config.UserConfig;
import com.dojoconsulting.gigawatt.core.IAccountManager;
import com.dojoconsulting.gigawatt.core.ITradeManager;
import com.dojoconsulting.gigawatt.core.IUserManager;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.User;
import com.dojoconsulting.oanda.fxtrade.api.UtilAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Amit
 * Date: 10-Oct-2007
 * Time: 00:03:38
 */
public class FXAccountManager implements IAccountManager {
    private List<Account> accounts;

    private ITradeManager tradeManager;
    private IUserManager userManager;

    public void init(final BackTestConfig config) {
        accounts = new ArrayList<Account>();
        final List<UserConfig> userConfigs = config.getUsers();

        for (final UserConfig u : userConfigs) {
            final List<AccountConfig> accountConfigs = u.getAccounts();
            final Vector<Account> accountVector = new Vector<Account>();
            for (final AccountConfig a : accountConfigs) {
                final Account account = UtilAPI.createAccount(a.getId(), a.getBalance(), a.getCurrency(), a.getName(), a.getCreatedate().getTime(), a.getLeverage());
                accounts.add(account);
                accountVector.add(account);
            }
            final User user = (User) userManager.getUser(u.getUsername());
            UtilAPI.setUserAccounts(user, accountVector);
        }
    }


    public Account getAccountWithId(final int accountId) {
        for (final Account acc : accounts) {
            if (acc.getAccountId() == (accountId)) {
                return acc;
            }
        }
        return null;
    }

    public void preTickProcess() {
        //nothing to do preprocess
    }

    
    public void postTickProcess() {
        for (final Account account : accounts) {
            UtilAPI.processAccount(account);
        }
    }

    public void setTradeManager(final ITradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    public void setUserManager(final IUserManager userManager) {
        this.userManager = userManager;
    }

//	private void processAccounts() {
//		for (final Account account : accounts) {
//			tradeManager.processTrades(account);
//		}
//	}

}
