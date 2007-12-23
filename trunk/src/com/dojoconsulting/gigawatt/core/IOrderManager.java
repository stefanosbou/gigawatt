package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.core.fximpl.domain.TransactionType;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.LimitOrder;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 11-Dec-2007
 * Time: 23:43:49
 */
public interface IOrderManager extends IEngineProcess {
	void executeOrder(LimitOrder newOrder, boolean isAbovePrice, Account account);

	void closeOrder(LimitOrder closeOrder, TransactionType transactionType);

	void modifyOrder(LimitOrder modifyOrder);
}
