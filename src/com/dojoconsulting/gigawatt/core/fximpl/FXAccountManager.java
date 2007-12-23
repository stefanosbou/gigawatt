package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.AccountConfig;
import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.config.UserConfig;
import com.dojoconsulting.gigawatt.core.Engine;
import com.dojoconsulting.gigawatt.core.IAccountManager;
import com.dojoconsulting.gigawatt.core.IUserManager;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.User;
import com.dojoconsulting.oanda.fxtrade.api.UtilAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	private static Log logger = LogFactory.getLog(FXAccountManager.class);

	private Account[] accounts;

	private IUserManager userManager;
	private Engine engine;

	public void init(final BackTestConfig config) {
		final List<Account> allAccounts = new ArrayList<Account>();
		final List<UserConfig> userConfigs = config.getUsers();

		for (final UserConfig u : userConfigs) {
			final List<AccountConfig> accountConfigs = u.getAccounts();
			final List<Account> accountVector = new Vector<Account>();
			for (final AccountConfig a : accountConfigs) {
				final Account account = UtilAPI.createAccount(a.getId(), a.getBalance(), a.getCurrency(), a.getName(), a.getCreatedate().getTime(), a.getLeverage(), a.getProcessType(), engine);
				allAccounts.add(account);
				accountVector.add(account);
			}
			final User user = (User) userManager.getUser(u.getUsername());
			UtilAPI.setUserAccounts(user, accountVector);
		}
		accounts = allAccounts.toArray(new Account[allAccounts.size()]);
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

	public void setEngine(final Engine engine) {
		this.engine = engine;
	}

	public void setUserManager(final IUserManager userManager) {
		this.userManager = userManager;
	}

	public void close() {
		for (final Account account : accounts) {
			logger.info(account);
		}
	}

}
