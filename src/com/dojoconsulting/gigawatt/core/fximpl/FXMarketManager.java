package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.config.MarketConfig;
import com.dojoconsulting.gigawatt.core.BackTestToolException;
import com.dojoconsulting.gigawatt.core.IMarketManager;
import com.dojoconsulting.gigawatt.data.IMarketData;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 13-Oct-2007
 * Time: 02:06:38
 */
public class FXMarketManager implements IMarketManager {

	private final Map<FXPair, IMarketData> markets;
	private final Map<FXPair, FXTick> tickTable;
	private final Multimap<FXPair, FXTick> perLoopTickTable;
	private boolean newTicksThisLoop;

	public FXMarketManager() {
		markets = new HashMap<FXPair, IMarketData>();
		tickTable = new HashMap<FXPair, FXTick>();
		perLoopTickTable = new ArrayListMultimap<FXPair, FXTick>();
	}

	public void init(final BackTestConfig config) {
		final List<MarketConfig> marketConfigs = config.getMarkets();
		String className = null;
		try {
			for (final MarketConfig marketConfig : marketConfigs) {
				final FXPair pair = new FXPair(marketConfig.getProduct());
				final String filename = marketConfig.getFilename();

				className = marketConfig.getClassname();
				final Class marketDataClass = Class.forName(className);
				final Constructor constructor = marketDataClass.getConstructor(FXPair.class, String.class);
				final IMarketData marketData = (IMarketData) constructor.newInstance(pair, filename);

				marketData.setMarketManager(this);
				marketData.init();
				markets.put(pair, marketData);
			}
		}
		catch (ClassNotFoundException e) {
			throw new BackTestToolException("FXMarketManager: There was a problem finding " + className + ".  Please ensure you have placed it on the classpath.", e);
		}
		catch (InvocationTargetException e) {
			throw new BackTestToolException("FXMarketManager: There was a problem instantiating " + className + ".  Please ensure you have a public constructor with signature of (FXPair pair, String dataFileName).", e);
		}
		catch (InstantiationException e) {
			throw new BackTestToolException("FXMarketManager: There was a problem instantiating " + className + ".  Please ensure you have a public constructor with signature of (FXPair pair, String dataFileName).", e);
		}
		catch (IllegalAccessException e) {
			throw new BackTestToolException("FXMarketManager: There was a problem instantiating " + className + ".  Please ensure you have a public constructor with signature of (FXPair pair, String dataFileName).", e);
		}
		catch (NoSuchMethodException e) {
			throw new BackTestToolException("FXMarketManager: There was a problem instantiating " + className + ".  Please ensure you have a public constructor with signature of (FXPair pair, String dataFileName).", e);
		}

		final Collection<IMarketData> marketValues = markets.values();
		for (final IMarketData marketData : marketValues) {
			perLoopTickTable.put((FXPair) marketData.getProduct(), null);
		}

	}

	public boolean newTicksThisLoop() {
		return this.newTicksThisLoop;
	}

	public boolean hasMoreTicks() {
		final Collection<IMarketData> marketValues = markets.values();
		for (final IMarketData marketData : marketValues) {
			if (marketData.hasMoreTicks()) {
				return true;
			}
		}
		return false;
	}

	public Map<FXPair, FXTick> getTickTable() {
		return tickTable;
	}

	public Multimap<FXPair, FXTick> getPerLoopTickTable() {
		return perLoopTickTable;
	}

	public void registerTick(final FXPair pair, final FXTick tick) {
		perLoopTickTable.put(pair, tick);
		newTicksThisLoop = true;
	}

	public void nextTick(final long currentTimeInMillis) {
		newTicksThisLoop = false;
		perLoopTickTable.clear();
		final Collection<IMarketData> marketValues = markets.values();
		for (final IMarketData marketData : marketValues) {
			final FXTick tick = (FXTick) marketData.getNextTick(currentTimeInMillis);
			if (tick != null && tick.getTimestamp() != 0) {
				tickTable.put((FXPair) marketData.getProduct(), tick);
			}
		}
	}
}
