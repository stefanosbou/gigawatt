package com.dojoconsulting.oanda.fxtrade.api;

/**
 * A TakeProfitOrder will close a MarketOrder when the designated market rate is reached.
 * <p/>
 * Please note that setting any field other than the price field will have no effect.
 */
public final class TakeProfitOrder extends Order implements Cloneable {

    public TakeProfitOrder() {
        super();
    }

    public TakeProfitOrder(final double price) {
        super();
        setPrice(price);
    }

    public void setPrice(final double price) {
        super.setPrice(price);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
