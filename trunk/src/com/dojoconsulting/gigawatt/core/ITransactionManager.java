package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;

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
}
