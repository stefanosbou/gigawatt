/**
 * 
 */

package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.IInterestRateManager;

/**
 * @author Nick Skaggs
 *
 */
public class FXInterestRateManager implements IInterestRateManager {

	// Read in interest rates into your new table and to create the table
	public void init(final BackTestConfig config) {
		//TODO: Anything to init for int rate manager?
	}
	
	public void close() {
		//TODO: Anything to close for int rate manager?
	}

	public Transaction calcInterestForClosedTrade(marketorder) {
		
	}
	public Transaction calcInterestForRolloverPosition(position) {
		
	}
	public Transaction calcInterestForAccount(account) {
		if mo.isopen() 
			return null; 
		startTime = mo.getTime();
		if startTime < 4PM_EST then startTime = 4PM_EST;
		endTime = mo.getClose().getTime();
		getAllInterestRatesFromDatabaseFor(startTime, endTime, mo.pair); calculateInterest;
	}
	public registerBalanceChange(account, oldBalance, newBalance) {
		
	}
}
