package com.dojoconsulting.oanda.fxtrade.api;

import java.util.Vector;

/**
 * The FXEventManager class keeps track of a set of FXEvents of a particular type, handling their registration,
 * notification, and deregistration.
 */
public abstract class FXEventManager {
    private Vector<FXEvent> events;

    FXEventManager() {
        events = new Vector<FXEvent>();
    }

    public boolean add(final FXEvent e) {
        return events.add(e);
    }

    public boolean remove(final FXEvent e) {
        return events.remove(e);
    }

    public Vector<FXEvent> getEvents() {
        return events;
    }


}
