package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 05:22:28
 */
public interface IMarketManager {

	boolean hasMoreTicks();

	boolean newTicksThisLoop();

	Map getTickTable();

	void init(BackTestConfig config);

	void nextTick(long time);

	void close();
}
