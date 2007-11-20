package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.Engine;
import com.dojoconsulting.gigawatt.core.IMarketManager;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.DefaultLocatorFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Account Tester.
 *
 * @author Amit Chada
 * @version 1.0
 * @since 10/27/2007
 */
public class AccountTest extends TestCase {
	public AccountTest(final String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetLeverage() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetAccountId() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetBalance() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetHomeCurrency() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetAccountName() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetPositions() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetOrders() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetPosition() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetPositionValue() throws Exception {
		final BeanFactoryLocator locator = DefaultLocatorFactory.getInstance();
		final BeanFactoryReference bf = locator.useBeanFactory("fxOandaEngineFactory");
		final Engine engine = (Engine) bf.getFactory().getBean("engine");

		final Map<FXPair, FXTick> tickTable = new HashMap<FXPair, FXTick>();
		tickTable.put(new FXPair("EUR/USD"), new FXTick(0, 0.9134, 0.9136));
		tickTable.put(new FXPair("EUR/CZK"), new FXTick(0, 2.2341, 2.2381));
		final IMarketManager mockManager = new IMarketManager() {


			public boolean hasMoreTicks() {
				return false;
			}

			public boolean newTicksThisLoop() {
				return false;
			}

			public Map getTickTable() {
				return tickTable;
			}

			public void init(final BackTestConfig config) {

			}

			public void nextTick(final long time) {

			}
		};

		engine.setMarketManager(mockManager);

		final Account account = new Account(1, 1000.0, "USD", "Test", 0, 50);
		final MarketOrder mo = new MarketOrder();
		mo.setPair(new FXPair("EUR/USD"));
		mo.setUnits(10000);
		account.execute(mo);

		final MarketOrder mo2 = new MarketOrder();
		mo2.setPair(new FXPair("EUR/CZK"));
		mo2.setUnits(-20000);
		account.execute(mo2);

		double result = account.getPositionValue();
		assertEquals("Incorrect PositionValue", 27404.0, result);

		final Account account2 = new Account(2, 1000.0, "USD", "Test", 0, 20);
		account2.execute(mo);
		account2.execute(mo2);

		result = account2.getPositionValue();
		assertEquals("Incorrect PositionValue", 27404.00, result);

	}

	public void testGetUnrealizedPL() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetOrderWithId() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetTradeWithId() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetTransactionWithId() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetCreateDate() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetMarginAvailable() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetMarginCallRate() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetMarginRate() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetMarginUsed() throws Exception {
		final BeanFactoryLocator locator = DefaultLocatorFactory.getInstance();
		final BeanFactoryReference bf = locator.useBeanFactory("fxOandaEngineFactory");
		final Engine engine = (Engine) bf.getFactory().getBean("engine");

		final Map<FXPair, FXTick> tickTable = new HashMap<FXPair, FXTick>();
		tickTable.put(new FXPair("EUR/USD"), new FXTick(0, 0.9134, 0.9136));
		tickTable.put(new FXPair("EUR/CZK"), new FXTick(0, 2.2341, 2.2381));
		final IMarketManager mockManager = new IMarketManager() {


			public boolean hasMoreTicks() {
				return false;
			}

			public boolean newTicksThisLoop() {
				return false;
			}

			public Map getTickTable() {
				return tickTable;
			}

			public void init(final BackTestConfig config) {

			}

			public void nextTick(final long time) {

			}
		};

		engine.setMarketManager(mockManager);

		final Account account = new Account(1, 1000.0, "USD", "Test", 0, 50);
		final MarketOrder mo = new MarketOrder();
		mo.setPair(new FXPair("EUR/USD"));
		mo.setUnits(10000);
		account.execute(mo);

		final MarketOrder mo2 = new MarketOrder();
		mo2.setPair(new FXPair("EUR/CZK"));
		mo2.setUnits(-20000);
		account.execute(mo2);

		double result = account.getMarginUsed();
		assertEquals("Incorrect marginUsed value", 913.44, result);

		final Account account2 = new Account(2, 1000.0, "USD", "Test", 0, 20);
		account2.execute(mo);
		account2.execute(mo2);

		result = account2.getMarginUsed();
		assertEquals("Incorrect marginUsed value", 1370.20, result);

	}

	public void testSetGetProfile() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetRealizedPL() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetTrades() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetTransactions() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetEventManager() throws Exception {
		//TODOTEST: Test goes here...
	}

	public void testGetNetAssetValue() throws Exception {
		//TODOTEST: Test goes here...
	}

	public static Test suite() {
		return new TestSuite(AccountTest.class);
	}
}
