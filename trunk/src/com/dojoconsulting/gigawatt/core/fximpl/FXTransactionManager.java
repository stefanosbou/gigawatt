package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.ITransactionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by IntelliJ IDEA.
 * User: Amit
 * Date: 01-Dec-2007
 * Time: 18:11:53
 */
public class FXTransactionManager implements ITransactionManager {

	private static Log logger = LogFactory.getLog(FXTransactionManager.class);

	private int nextTicketNumber = 1;
	private DataSource dataSource;

	public void init(final BackTestConfig config) {
		long timestamp = 0;
		if (logger.isDebugEnabled()) {
			timestamp = System.currentTimeMillis();
			logger.debug("FXTransactionManager: Started init()");
		}
		try {
			final Connection orderDB = dataSource.getConnection();

			final Statement st = orderDB.createStatement();
			st.executeUpdate("DROP TABLE transactions IF EXISTS");
			final String expression = "CREATE TABLE transactions ( transactionNumber INTEGER, accountId INTEGER, market CHAR(7), isLong BOOLEAN, isAbovePrice BOOLEAN, price FLOAT, stopLoss FLOAT, takeProfit FLOAT, expiry BIGINT)";
			st.executeUpdate(expression);
			st.close();

		}
		catch (SQLException e) {
			e.printStackTrace();  // Todoerror: Improve error handling
			System.exit(1);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("FXTransactionManager: Finished init() in " + (System.currentTimeMillis() - timestamp));
		}
	}

	public void close() {
	}

	public int getNextTransactionNumber() {
		return nextTicketNumber++;
	}

	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
