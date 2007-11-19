package com.dojoconsulting.oanda.fxtrade.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * The RateTable object holds all incoming rate information.  A ratetable is used to obtain rate info (quotes for
 * currency pairs) and history data for these as well.
 */
public final class RateTable {
    private Map tickTable;

    private FXEventManager eventManager = new FXEventManager() {
        final void event(final FXRateEventInfo ei) {
            for (final FXEvent event : getEvents()) {
                if (event instanceof FXRateEvent) {
                    final FXRateEvent rateEvent = (FXRateEvent) event;
                    if (rateEvent.match(ei)) {
                        event.handle(ei, this);
                    }
                }
            }
        }
    };


    public RateTable() {
        this.tickTable = new HashMap();
    }

    public FXTick getRate(final FXPair pair) throws RateTableException {
        return (FXTick) tickTable.get(pair);
    }

    void setTickTable(final Map tickTable) {
        this.tickTable = tickTable;
    }

    public FXEventManager getEventManager() {
        return eventManager;
    }

    public Vector getCandles(final FXPair pair, final long interval, final int numTicks) throws OAException {
        //todo: Implement getCandles()
        return null;
    }

    public Vector getHistory(final FXPair pair, final long interval, final int numTicks) throws OAException {
        //todo: Implement getHistory()
        return null;
    }

    public Vector getMinMaxs(final FXPair pair, final long interval, final int numTicks) throws OAException {
        //todo: Implement getMinMaxs()
        return null;
    }

    public boolean loggedIn() {
        return true;
    }

}
