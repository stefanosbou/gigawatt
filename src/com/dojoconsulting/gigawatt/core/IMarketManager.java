package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;
import com.google.common.collect.Multimap;

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

	Multimap<FXPair, FXTick> getPerLoopTickTable();

	long getTickCounter();
}
