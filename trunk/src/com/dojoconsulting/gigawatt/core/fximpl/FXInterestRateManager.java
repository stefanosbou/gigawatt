/**
 *
 */

package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.IInterestRateManager;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;
import com.dojoconsulting.oanda.fxtrade.api.Position;
import com.dojoconsulting.oanda.fxtrade.api.Transaction;

/**
 * @author Nick Skaggs
 */
public class FXInterestRateManager implements IInterestRateManager {

	// Read in interest rates into your new table and to create the table
	public void init(final BackTestConfig config) {
		//TODO: Anything to init for int rate manager?
		// Comment by AmitChada: You will need to get the interest rate file and want to create the db tables.
	}

	public Transaction calcInterestForClosedTrade(final MarketOrder marketOrder) {
//		if mo.isopen()
//			return null;
//		startTime = mo.getTime();
//		if startTime < 4PM_EST then startTime = 4PM_EST;
//		endTime = mo.getClose().getTime();
//		getAllInterestRatesFromDatabaseFor(startTime, endTime, mo.pair); calculateInterest;
		return null;
	}

	public Transaction calcInterestForRolloverPosition(final Position position) {
		return null;
	}

	public Transaction calcInterestForAccount(final Account account) {
		return null;
	}

	public void registerBalanceChange(final Account account, final double oldBalance, final double newBalance) {

	}

	public void close() {
		//TODO: Anything to close for int rate manager?
		// Comment by AmitChada: Close the db connection is all I can think of
	}

}
