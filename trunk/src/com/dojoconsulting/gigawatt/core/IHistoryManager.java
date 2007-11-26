package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.data.ITick;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 25-Nov-2007
 * Time: 15:24:34
 */
public interface IHistoryManager {

	void init(BackTestConfig config);

	void registerTick(FXPair pair, ITick tick);

	List getHistory(FXPair pair, long interval, int numTicks);
}
