package com.dojoconsulting.oanda.fxtrade.api;

/**
 * The FXAccountEvent class is the abstract superclass of all events which will be fired in response to a new
 * transaction done on an account.  An FXAccountEvent can filter incoming transactions by setting a key corresponding
 * to a desired transaction's getType() value.  If a key is set, the match method will only be called for transactions
 * with the given type.
 */
public abstract class FXAccountEvent extends FXEvent {
    private String key;

    public FXAccountEvent(final boolean t) {
        super(t);
    }

    public FXAccountEvent() {
        super();
        key = null;
    }

    public FXAccountEvent(final String key) {
        this.key = key;
    }

    public FXAccountEvent(final String key, final boolean t) {
        super(t);
        this.key = key;
    }

    public abstract void handle(FXEventInfo EI, FXEventManager EM);

    public boolean match(final FXEventInfo EI) {
        if (!(EI instanceof FXAccountEventInfo)) {
            return false;
        }
        final FXAccountEventInfo aEI = (FXAccountEventInfo) EI;
        final String transactionType = aEI.getTransaction().getType();
        return transactionType.equals(key);
    }
}
