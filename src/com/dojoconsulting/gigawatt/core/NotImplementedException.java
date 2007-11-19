package com.dojoconsulting.gigawatt.core;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 17-Oct-2007
 * Time: 02:43:18
 * Exception is thrown by methods that exist on the real OANDA API but are not supported by this backtesting tool.
 */
public class NotImplementedException extends RuntimeException {
    public NotImplementedException() {
        super();
    }

    public NotImplementedException(final String s) {
        super(s);
    }

    public NotImplementedException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public NotImplementedException(final Throwable throwable) {
        super(throwable);
    }
}
