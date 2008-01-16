/**
 *
 */
package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;
import com.dojoconsulting.oanda.fxtrade.api.Position;
import com.dojoconsulting.oanda.fxtrade.api.Transaction;

/**
 * @author Nick Skaggs
 */
public interface IInterestRateManager {

	void close();

	void init(BackTestConfig config);

	Transaction calcInterestForClosedTrade(final MarketOrder marketOrder);

	Transaction calcInterestForPosition(final Position position);
	
	void calcInterestForRollover();

	Transaction calcInterestForAccount(final Account account);
}
