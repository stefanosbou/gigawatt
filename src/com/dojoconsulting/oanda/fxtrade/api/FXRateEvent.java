package com.dojoconsulting.oanda.fxtrade.api;

/**
 * The FXRateEvent class is the abstract superclass of all events which will be fired in response to a change in current
 * market rates.  An FXRateEvent can filter incoming rates by setting a key corresponding to a desired currency pair.  If
 * key is set, the match method will only be called for rate changes in the specified pair.
 */
public abstract class FXRateEvent extends FXEvent {
    private String key;

    public FXRateEvent(final boolean b) {
        super(b);
    }

    public FXRateEvent() {
        super();
        key = null;
    }

    public FXRateEvent(final String key) {
        this.key = key;
    }

    public FXRateEvent(final String key, final boolean t) {
        super(t);
        this.key = key;
    }

    public abstract void handle(FXEventInfo EI, FXEventManager EM);

    public boolean match(final FXEventInfo EI) {
        if (!(EI instanceof FXRateEventInfo)) {
            return false;
        }
        final FXRateEventInfo rateEventInfo = (FXRateEventInfo) EI;
        final String pair = rateEventInfo.getPair().getPair();
        return pair.equals(key);
    }

}
