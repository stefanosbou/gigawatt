package com.dojoconsulting.gigawatt.core;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 23-Oct-2007
 * Time: 05:59:37
 * To change this template use File | Settings | File Templates.
 */
public class BackTestToolException extends RuntimeException {
    private Throwable specificAPIException;

    public BackTestToolException() {
        super();
    }

    public BackTestToolException(final String message) {
        super(message);
    }

    public BackTestToolException(final String message, final Throwable specificAPIException) {
        super(message);
        this.specificAPIException = specificAPIException;
    }

    public Throwable getSpecificAPIException() {
        return specificAPIException;
    }
}
