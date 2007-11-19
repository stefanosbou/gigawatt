package com.dojoconsulting.gigawatt.data;

import com.dojoconsulting.gigawatt.core.IMarketManager;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 07:15:52
 * To change this template use File | Settings | File Templates.
 */
public interface IMarketData {
    boolean hasMoreTicks();

    ITick getNextTick(long currentTimeInMillis);

    IProduct getProduct();

    void setMarketManager(IMarketManager marketManager);
}
