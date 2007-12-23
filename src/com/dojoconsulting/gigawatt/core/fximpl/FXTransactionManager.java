package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.ITransactionManager;

/**
 * Created by IntelliJ IDEA.
 * User: Amit
 * Date: 01-Dec-2007
 * Time: 18:11:53
 */
public class FXTransactionManager implements ITransactionManager {

	private int nextTicketNumber = 1;

	public void init(final BackTestConfig config) {
	}

	public void close() {
	}

	public int getNextTransactionNumber() {
		return nextTicketNumber++;
	}
}
