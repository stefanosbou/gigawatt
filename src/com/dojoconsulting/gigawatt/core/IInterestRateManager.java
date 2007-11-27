/**
 * 
 */
package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;

/**
 * @author Nick Skaggs
 *
 */
public interface IInterestRateManager {

	void close();

	void init(BackTestConfig config);

	public Transaction calcInterestForClosedTrade(marketorder); 
	public Transaction calcInterestForRolloverPosition(position); 
	public Transaction calcInterestForAccount(account); 
	public registerBalanceChange(account, oldBalance, newBalance);
}
