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

	Transaction calcInterestForRolloverPosition(final Position position);

	Transaction calcInterestForAccount(final Account account);

	void registerBalanceChange(final Account account, final double oldBalance, final double newBalance);
}
