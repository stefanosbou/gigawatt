package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.oanda.fxtrade.api.Account;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 05:21:03
 */
public interface IAccountManager extends IEngineProcess {
	Account getAccountWithId(int accountNumber);
}
