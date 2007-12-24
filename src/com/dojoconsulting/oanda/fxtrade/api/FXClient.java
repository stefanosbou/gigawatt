package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.Engine;
import com.dojoconsulting.gigawatt.core.GigawattException;
import com.dojoconsulting.gigawatt.core.NotImplementedException;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;

import java.util.Map;
import java.util.Observable;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 08:00:00
 */
public abstract class FXClient extends Observable {

	public static final long INTERVAL_1_DAY = 86400000L;
	public static final long INTERVAL_1_HOUR = 3600000L;
	public static final long INTERVAL_1_MIN = 60000L;
	public static final long INTERVAL_10_SEC = 10000L;
	public static final long INTERVAL_15_MIN = 900000L;
	public static final long INTERVAL_3_HOUR = 10800000L;
	public static final long INTERVAL_30_MIN = 1800000L;
	public static final long INTERVAL_30_SEC = 30000L;
	public static final long INTERVAL_5_MIN = 300000L;
	public static final long INTERVAL_5_SEC = 5000L;
	public static final String VERSION_INFO = "2004_06_16";

	public static final String CONNECTED = "CONNECTED";
	public static final String DISCONNECTED = "DISCONNECTED";
	public static final String FATAL_ERROR = "FATAL_ERROR";
	public static final String UPDATE = "UPDATE";

	private static Engine engine;

	private boolean withRateThread;
	private boolean withKeepAliveThread;

	private RateTable rateTable;
	private User user;

	public FXClient() {
		rateTable = new RateTable();
	}

	public RateTable getRateTable() throws SessionDisconnectedException {
		lazyLoad();
		final Map tickTable = engine.getMarketManager().getTickTable();
		rateTable.setTickTable(tickTable);
		//TODO: Improve this.
		rateTable.setHistoryManager(engine.getHistoryManager());
		return rateTable;
	}

	public Vector getHistory(final FXPair pair, final long interval, final int numTicks) throws OAException {
		throw new NotImplementedException("fxclient.getHistory() is not supported in this version of the FXOandaBackTest tool.");
	}


	private void lazyLoad() {
		if (engine == null) {
			final BeanFactoryLocator bfLocator = SingletonBeanFactoryLocator.getInstance("com/dojoconsulting/gigawatt/config/beanRefContext.xml");
			final BeanFactoryReference bf = bfLocator.useBeanFactory("fxOandaEngineFactory");
			engine = (Engine) bf.getFactory().getBean("engine");
		}
	}

	public long getServerTime() {
		return engine.getTimeServer().getTime();
	}

	public User getUser() throws SessionException {
		if (!isLoggedIn()) {
			throw new SessionException("No user logged in.");
		}
		return user;
	}


	public boolean isLoggedIn() {
		return user != null;
	}

	public void login(final String userName, final String password) throws InvalidUserException, InvalidPasswordException, SessionException {
		lazyLoad();
		try {
			if (engine.getUserManager().verifyLogin(userName, password)) {
				this.user = (User) engine.getUserManager().getUser(userName);
			} else {
				throw new InvalidPasswordException("Invalid password for " + userName);
			}
		}
		catch (GigawattException e) {
			throw (InvalidUserException) e.getSpecificAPIException();
		}
	}

	public void logout() {
		this.user = null;
	}

	public void setProxy(final boolean state) {
		//does nothing.
	}

	public boolean getWithRateThread() {
		return withRateThread;
	}

	public boolean getWithKeepAliveThread() {
		return withKeepAliveThread;
	}

	public void setTimeout(final int timeout) {
		//does nothing
	}

	public void setWithRateThread(final boolean withRateThread) {
		this.withRateThread = withRateThread;
	}

	public void setWithKeepAliveThread(final boolean withKeepAliveThread) {
		this.withKeepAliveThread = withKeepAliveThread;
	}

	protected abstract String getClientType();
}
