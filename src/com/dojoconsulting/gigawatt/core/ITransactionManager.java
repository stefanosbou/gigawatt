package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.fximpl.domain.TransactionType;
import com.dojoconsulting.oanda.fxtrade.api.Account;
import com.dojoconsulting.oanda.fxtrade.api.AccountException;
import com.dojoconsulting.oanda.fxtrade.api.MarketOrder;
import com.dojoconsulting.oanda.fxtrade.api.Transaction;

/**
 * Created by IntelliJ IDEA.
 * User: Amit
 * Date: 01-Dec-2007
 * Time: 18:12:18
 */
public interface ITransactionManager {

	void init(BackTestConfig config);

	void close();

	int getNextTransactionNumber();

	Transaction createExecuteMarketTransaction(MarketOrder mo, Account account, TransactionType transactionType) throws AccountException;

	Transaction createCloseTransaction(MarketOrder mo, Account account, TransactionType transactionType, double realizedPL, double interest);

	// -------------------  TRADE ----------------------
//Done:  Buy Market
//TODO :  Buy Market Filled
//Done :  Close Position
//Done :  Close Trade
//Done :  Stop Loss
//Done :  Take Profit
//Done :  Margin Call
//Done :  Sell Market
//TODO :  Sell Market Filled

	// -------------------  ORDER ----------------------
//TODO :  Buy Order
//TODO :  Change Margin
//TODO :  Change Order
//TODO :  Change Trade
//TODO :  Order Cancelled
//TODO :  Order Expired
//TODO :  Order Filled
//TODO :  Order Cancelled (NSF)
//TODO :  Order Cancelled (BV)
//TODO :  Order Cancelled (BV:SL)
//TODO :  Order Cancelled (BV:TP)

	// -------------------  ADMIN ----------------------
//TODO :  Fund Deposit
//TODO :  API Fee
//TODO :  API License Fee
//TODO :  Fund Credit
//TODO :  Fund Withdrawl
//TODO :  Wire Fee
//TODO :  Interest
//TODO :  Trade Correction
//TODO :  Trade Cancel


}
