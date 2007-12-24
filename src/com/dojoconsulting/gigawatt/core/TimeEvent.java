package com.dojoconsulting.gigawatt.core;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 24-Dec-2007
 * Time: 03:48:14
 */
public abstract class TimeEvent {

	private long timeForEvent = 0;
	private long recurrence = 0;

	public final long getTimeForEvent() {
		return timeForEvent;
	}

	public final void setTimeForEvent(final long timeForEvent) {
		this.timeForEvent = timeForEvent;
	}

	public final void setRecurrence(final long recurrence) {
		this.recurrence = recurrence;
	}

	public final long getRecurrence() {
		return recurrence;
	}

	public abstract void handle(final long timeForEvent);

}
