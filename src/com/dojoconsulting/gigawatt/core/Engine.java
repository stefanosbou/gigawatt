package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Amit
 * Date: 09-Oct-2007
 * Time: 23:55:33
 */
public class Engine {

	private IAccountManager accountManager;
	private IMarketManager marketManager;
	private ITradeManager tradeManager;
	private IUserManager userManager;
	private IStrategyManager strategyManager;
	private IHistoryManager historyManager;
	private IInterestRateManager interestRateManager;
	private ITransactionManager transactionManager;
	private IOrderManager orderManager;

	private static Log log = LogFactory.getLog(Engine.class);

	private boolean running;
	private long logStartTime;

	private TimeServer timeServer;

	public void init() {
		final BackTestConfig config = BackTestConfig.load();

		timeServer.init(config, this);
		log.info("Start date is: " + timeServer.getStartDateAsString() + " (" + timeServer.getTime() + ")");
		if (timeServer.getEndDate() == null) {
			log.info("End date is not specified.  Will continue until all ticks are processed.");
		} else {
			log.info("End date is: " + timeServer.getEndDateAsString() + " (" + timeServer.getEndDateAsMillis() + ")");
		}

		// UserManager must go before AccountManager
		userManager.init(config);
		// AccountManager must go after UserManager
		accountManager.init(config);

		tradeManager.init(config);
		marketManager.init(config);
		historyManager.init(config);
		interestRateManager.init(config);
		transactionManager.init(config);
		orderManager.init(config);

		// Strategy must go last
		strategyManager.init(config);
		running = false;
	}

	public void start() {
		if (!running) {
			init();
			final Worker worker = new Worker();
			worker.start();
		}
	}

	public void stop() {
		running = false;
	}

	private void loop() {
		started();
		running = true;
		while (marketManager.hasMoreTicks() && running) {

			// pre Tick process
			tradeManager.preTickProcess();
			orderManager.preTickProcess();

			// update Ticks
			final long currentTime = timeServer.processNextLoop();
			marketManager.nextTick(currentTime);

			//post Tick process
			tradeManager.postTickProcess();
			orderManager.postTickProcess();
			accountManager.postTickProcess();
			strategyManager.postTickProcess();
		}

		running = false;
		stopped();
	}

	private void started() {
		log.info("FXOandaBackTest has started.");
		logStartTime = new Date().getTime();
	}

	private void stopped() {
		accountManager.close();
		userManager.close();
		tradeManager.close();
		orderManager.close();
		interestRateManager.close();
		strategyManager.close();
		marketManager.close();
		transactionManager.close();

		log.info("FXOandaBackTest has finished.");
		final long totalTime = new Date().getTime() - logStartTime;
		log.info("Test took " + totalTime);
		final long ticksPerSecond = marketManager.getTickCounter() / (totalTime / 1000);
		log.info("TicksPerSecond: " + ticksPerSecond);
		if (!log.isInfoEnabled()) {
			System.out.println("Test took " + totalTime);
		}
	}

	private class Worker extends Thread {
		public void run() {
			try {
				loop();
			}
			catch (GigawattException e) {
				log.error("An error has occured that has ceased the running of Gigawatt.", e);
				if (e.getSpecificAPIException() != null) {
					log.error("The underlying exception was:", e.getSpecificAPIException());
				}
				stopped();
				System.exit(1);
			}
		}
	}

	/* Spring beans */

	public void setTimeServer(final TimeServer timeServer) {
		this.timeServer = timeServer;
	}

	public void setOrderManager(final IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public void setTradeManager(final ITradeManager tradeManager) {
		this.tradeManager = tradeManager;
	}

	public void setStrategyManager(final IStrategyManager strategyManager) {
		this.strategyManager = strategyManager;
	}

	public void setUserManager(final IUserManager userManager) {
		this.userManager = userManager;
	}

	public void setMarketManager(final IMarketManager marketManager) {
		this.marketManager = marketManager;
	}

	public void setAccountManager(final IAccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void setInterestRateManager(final IInterestRateManager interestRateManager) {
		this.interestRateManager = interestRateManager;
	}

	public void setHistoryManager(final IHistoryManager historyManager) {
		this.historyManager = historyManager;
	}

	public void setTransactionManager(final ITransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public TimeServer getTimeServer() {
		return timeServer;
	}

	public IMarketManager getMarketManager() {
		return marketManager;
	}

	public IUserManager getUserManager() {
		return userManager;
	}

	public ITradeManager getTradeManager() {
		return tradeManager;
	}

	public IAccountManager getAccountManager() {
		return accountManager;
	}

	public IStrategyManager getStrategyManager() {
		return strategyManager;
	}

	public IHistoryManager getHistoryManager() {
		return historyManager;
	}

	public IInterestRateManager getInterestRateManager() {
		return interestRateManager;
	}

	public ITransactionManager getTransactionManager() {
		return transactionManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}
	/* End of Spring beans */

}
