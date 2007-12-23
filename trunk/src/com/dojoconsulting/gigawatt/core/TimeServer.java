/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 17-Oct-2007
 * Time: 03:45:28
 */
package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServer {
	private static TimeServer instance = new TimeServer();
	private static int TIME_INCREMENT;
	private String startDateAsString;
	private String endDateAsString;
	private long startDateAsMillis;
	private long endDateAsMillis;
	private Date startDate;
	private Date endDate;

	private boolean processEndDate;

	private Engine engine;

	public static TimeServer getInstance() {
		return instance;
	}

	private TimeServer() {
	}

	private long currentTimeInMillis;

	void init(final BackTestConfig config, final Engine engine) {
		this.engine = engine;
		startDate = config.getStartdate();
		endDate = config.getEnddate();
		startDateAsString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate);
		currentTimeInMillis = startDate.getTime();
		startDateAsMillis = currentTimeInMillis;

		if (endDate != null) {
			endDateAsString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endDate);
			endDateAsMillis = endDate.getTime();
			processEndDate = true;
		} else {
			endDateAsString = "";
			endDateAsMillis = 0;
			processEndDate = false;
		}

		TIME_INCREMENT = config.getIncrement();
	}

	public long getTime() {
		return currentTimeInMillis;
	}

	public long processNextLoop() {
		currentTimeInMillis += TIME_INCREMENT;
		if (processEndDate) {
			if (currentTimeInMillis > endDateAsMillis) {
				engine.stop();
			}
		}
		return currentTimeInMillis;
	}

	public String getStartDateAsString() {
		return startDateAsString;
	}

	public String getEndDateAsString() {
		return endDateAsString;
	}

	public boolean isBeforeStart(final long millis) {
		return millis < startDateAsMillis;
	}

	public boolean isAfterStart(final long millis) {
		return millis > startDateAsMillis;
	}

	public long getTimeSinceStart() {
		return currentTimeInMillis - startDateAsMillis;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public long getEndDateAsMillis() {
		return endDateAsMillis;
	}
}
