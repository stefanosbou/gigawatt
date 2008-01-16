/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 17-Oct-2007
 * Time: 03:45:28
 */
package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class TimeServer {
	public static int TIME_INCREMENT;
	private static Log logger = LogFactory.getLog(TimeServer.class);

	private String startDateAsString;
	private String endDateAsString;
	private long startDateAsMillis;
	private long endDateAsMillis;
	private Date startDate;
	private Date endDate;

	private boolean processEndDate;

	private DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Engine engine;

	private List<TimeEvent> timeEvents;
	private long nextTimeEvent = 0;
	public static final int MINUTE_IN_MILLI = 1000 * 60;
	public static final int HOUR_IN_MILLI = MINUTE_IN_MILLI * 60;
	public static final int DAY_IN_MILLI = HOUR_IN_MILLI * 24;
	public static final int WEEK_IN_MILLI = DAY_IN_MILLI * 7;

	public TimeServer() {
	}

	private long currentTimeInMillis;

	void init(final BackTestConfig config, final Engine engine) {
		this.engine = engine;
		startDate = config.getStartdate();
		endDate = config.getEnddate();
		startDateAsString = fullDateFormat.format(startDate);
		currentTimeInMillis = startDate.getTime();
		startDateAsMillis = currentTimeInMillis;

		if (endDate != null) {
			endDateAsString = fullDateFormat.format(endDate);
			endDateAsMillis = endDate.getTime();
			processEndDate = true;
		} else {
			endDateAsString = "";
			endDateAsMillis = 0;
			processEndDate = false;
		}

		timeEvents = new ArrayList<TimeEvent>();

		if (logger.isDebugEnabled()) {
			final TimeEvent processingTimeEvent = new TimeEvent() {
				public void handle(final long timeForEvent) {
					printProgress(timeForEvent);
				}
			};
			processingTimeEvent.setRecurrence(DAY_IN_MILLI);
			processingTimeEvent.setTimeForEvent(startDateAsMillis - (startDateAsMillis % DAY_IN_MILLI));

			addTimeEvent(processingTimeEvent);
		}
		TIME_INCREMENT = config.getIncrement();
	}

	private void calculateNextTimeEvent() {
		if (timeEvents.isEmpty()) {
			nextTimeEvent = 0;
			return;
		}
		nextTimeEvent = Long.MAX_VALUE;
		for (final TimeEvent timeEvent : timeEvents) {
			nextTimeEvent = Math.min(nextTimeEvent, timeEvent.getTimeForEvent());
		}
	}

	private void processTimeEvents() {
		for (int i = timeEvents.size(); i > 0; i--) {
			final TimeEvent timeEvent = timeEvents.get(i - 1);
			final long timeForEvent = timeEvent.getTimeForEvent();
			final long recurrance = timeEvent.getRecurrence();
			if (currentTimeInMillis >= timeForEvent) {
				timeEvent.handle(timeForEvent);
				if (recurrance > 0) {
					timeEvent.setTimeForEvent(timeForEvent + recurrance);
				} else {
					removeTimeEvent(timeEvent, false);
				}
			}
		}
		logger.info("Processed " + timeEvents.size() + " time events at " + currentTimeInMillis);
		calculateNextTimeEvent();
	}


	public long getTimeInMilliFor(final String time) {
		try {
			final Date currentDate = new Date(currentTimeInMillis);
			final String currentDateAsString = shortDateFormat.format(currentDate);

			final String requestedDateAsString = currentDateAsString + " " + time;
			final Date requestedDate = fullDateFormat.parse(requestedDateAsString);

			final Calendar currentCalendar = new GregorianCalendar();
			currentCalendar.setTime(currentDate);
			final Calendar requestedCalendar = new GregorianCalendar();
			requestedCalendar.setTime(requestedDate);

			if (currentCalendar.after(requestedCalendar)) {
				requestedCalendar.add(Calendar.DATE, 1);
			}

			return requestedCalendar.getTimeInMillis();
		} catch (ParseException e) {
			throw new GigawattException("Problem attempting to parse time when calculating TimeInMillisFor(" + time + ")", e);
		}
	}

	public void addTimeEvent(final TimeEvent timeEvent) {
		if (timeEvent.getTimeForEvent() == 0) {
			throw new GigawattException("No time for event set on TimeEvent");
		}
		timeEvents.add(timeEvent);
		calculateNextTimeEvent();
	}

	public void changeTimeEvent(final TimeEvent timeEvent) {
		if (timeEvent.getTimeForEvent() == 0) {
			throw new GigawattException("No time for event set on TimeEvent");
		}
		if (!timeEvents.contains(timeEvent)) {
			timeEvents.add(timeEvent);
		}
		calculateNextTimeEvent();
	}

	private void removeTimeEvent(final TimeEvent timeEvent, final boolean recalculateNextTimeEvent) {
		timeEvents.remove(timeEvent);
		if (recalculateNextTimeEvent) {
			calculateNextTimeEvent();
		}
	}

	public void removeTimeEvent(final TimeEvent timeEvent) {
		removeTimeEvent(timeEvent, true);
	}

	private void printProgress(final long timeForEvent) {
		logger.info("Processing " + shortDateFormat.format(new Date(timeForEvent)));
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
		if (nextTimeEvent != 0) {
			if (currentTimeInMillis >= nextTimeEvent) {
				processTimeEvents();
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
