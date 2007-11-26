package com.dojoconsulting.oanda.fxtrade.api;

import java.util.ArrayList;
import java.util.List;

/**
 * The FXEventManager class keeps track of a set of FXEvents of a particular type, handling their registration,
 * notification, and deregistration.
 */
public abstract class FXEventManager {
	private List<FXEvent> events;

	FXEventManager() {
		events = new ArrayList<FXEvent>();
	}

	public boolean add(final FXEvent e) {
		return events.add(e);
	}

	public boolean remove(final FXEvent e) {
		return events.remove(e);
	}

	public List getEvents() {
		return events;
	}
}
