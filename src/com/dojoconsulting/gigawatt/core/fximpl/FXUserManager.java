package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.config.UserConfig;
import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.gigawatt.core.IUser;
import com.dojoconsulting.gigawatt.core.IUserManager;
import com.dojoconsulting.oanda.fxtrade.api.InvalidUserException;
import com.dojoconsulting.oanda.fxtrade.api.User;
import com.dojoconsulting.oanda.fxtrade.api.UtilAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 22-Oct-2007
 * Time: 23:47:18
 */
public class FXUserManager implements IUserManager {
	private Map<String, User> users;

	public IUser getUser(final String userName) {
		return users.get(userName);
	}


	public boolean verifyLogin(final String userName, final String password) {
		if (!users.containsKey(userName)) {
			final InvalidUserException invalidUserName = new InvalidUserException(userName + " is an invalid username.");
			throw new GigawattException("InvalidUserException for " + userName, invalidUserName);
		}
		final User user = users.get(userName);
		return user.getPassword().equals(password);

	}

	public void init(final BackTestConfig config) {
		users = new HashMap<String, User>();

		final List<UserConfig> userConfigs = config.getUsers();

		for (final UserConfig u : userConfigs) {
			final User user = UtilAPI.createUser(u.getId(), u.getUsername(), u.getPassword(), u.getName(), u.getAddress(), u.getTelephone(), u.getEmail(), u.getCreatedate().getTime());
			users.put(user.getUserName(), user);
		}
	}

	public void preTickProcess() {
		//nothing to do
	}

	public void postTickProcess() {
		//nothing to do
	}

	public void close() {
		// nothing to do
	}
}
