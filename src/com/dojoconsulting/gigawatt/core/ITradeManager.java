package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.core.fximpl.domain.TransactionType;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 05:23:12
 */
public interface ITradeManager extends IEngineProcess {

	void closeTrade(MarketOrder closeTrade, TransactionType transactionType);

	void modifyTrade(MarketOrder mo);

	void executeTrade(MarketOrder mo, Account account, TransactionType transactionType);
}
