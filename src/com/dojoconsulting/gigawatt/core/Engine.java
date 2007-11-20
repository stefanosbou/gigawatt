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

    private Log log = LogFactory.getLog(Engine.class);

    private boolean running;
    private long logStartTime;

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

    public Engine() {
    }

    public void init() {
        final BackTestConfig config = BackTestConfig.load();

        final TimeServer timeServer = TimeServer.getInstance();
        timeServer.init(config);
        log.info("Start date is: " + timeServer.getStartDateAsString() + " (" + timeServer.getTime() + ")");

        // UserManager must go before AccountManager
        userManager.init(config);
        // AccountManager must go after UserManager
        accountManager.init(config);

        tradeManager.init(config);
        marketManager.init(config);

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
        final TimeServer timeServer = TimeServer.getInstance();
        while (marketManager.hasMoreTicks() && running) {
            long currentTime = timeServer.getTime();

            // pre Tick process
//			userManager.preTickProcess();
//			accountManager.preTickProcess();
			tradeManager.preTickProcess();
//			strategyManager.preTickProcess();

            // update Ticks
            currentTime = timeServer.processNextLoop();
            marketManager.nextTick(currentTime);

            //post Tick process
//			userManager.postTickProcess();
            tradeManager.postTickProcess();
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
        log.info("FXOandaBackTest has finished.");
        final long totalTime = new Date().getTime() - logStartTime;
        log.info("Test took " + totalTime);
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

    private class Worker extends Thread {
        public void run() {
            try {
                loop();
            }
            catch (BackTestToolException e) {
                log.error("An error has occured that has ceased the running of Gigawatt.", e);
                if (e.getSpecificAPIException() != null) {
                    log.error("The underlying exception was:", e.getSpecificAPIException());
                }
                stopped();
                System.exit(1);
            }
        }
    }

}