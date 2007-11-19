package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 22-Oct-2007
 * Time: 23:00:12
 * To change this template use File | Settings | File Templates.
 */
public interface IEngineProcess {
    void init(final BackTestConfig config);

    void preTickProcess();

    void postTickProcess();
}
