package com.dojoconsulting.oanda.fxtrade.api;

/**
 * FXEvent is the abstract superclass of all events.
 */
public abstract class FXEvent {
    private boolean isTransient;

    public FXEvent(final boolean t) {
        isTransient = t;
    }

    protected FXEvent() {
        isTransient = false;
    }

    public final void setTransient(final boolean b) {
        isTransient = b;

    }

    public final boolean isTransient() {
        return isTransient;
    }

    public abstract void handle(FXEventInfo EI, FXEventManager EM);

    public abstract boolean match(FXEventInfo EI);
}
