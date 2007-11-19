package com.dojoconsulting.oanda.fxtrade.api;

/**
 * A StopLossOrder will close a MarketOrder when the designated market rate is reached.
 * <p/>
 * Please note that setting any field other than the price field will have no effect.
 */
public final class StopLossOrder extends Order implements Cloneable {

    public StopLossOrder() {
        super();
    }

    public StopLossOrder(final double price) {
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
