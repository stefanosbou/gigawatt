package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Amit
 * Date: 16-Jan-2008
 * Time: 01:58:27
 * To change this template use File | Settings | File Templates.
 */
public class TimeServerTest extends TestCase {

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void testGetTimeInMillisForSameDay() throws ParseException {

		final TimeServer time = new TimeServer();

		final BackTestConfig config = new BackTestConfig();
		final String date = "2007-10-10 13:00:00";
		final Date testDate = dateFormat.parse(date);

		config.setStartdate(testDate);
		time.init(config, null);

		final long startTime = testDate.getTime();
		final long expectedTime = startTime + (3 * 60 * 60 * 1000);

		final long actualTime = time.getTimeInMilliFor("16:00:00");
		assertEquals(expectedTime, actualTime);
	}

	public void testGetTimeInMillisForNextDay() throws ParseException {

		final TimeServer time = new TimeServer();

		final BackTestConfig config = new BackTestConfig();
		final String date = "2007-10-10 18:00:00";
		final Date testDate = dateFormat.parse(date);

		config.setStartdate(testDate);
		time.init(config, null);

		final long startTime = testDate.getTime();
		final long expectedTime = startTime + (22 * 60 * 60 * 1000);

		final long actualTime = time.getTimeInMilliFor("16:00:00");
		assertEquals(expectedTime, actualTime);
	}
}
