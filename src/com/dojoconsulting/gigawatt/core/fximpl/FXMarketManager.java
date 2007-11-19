package com.dojoconsulting.gigawatt.core.fximpl;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.config.MarketConfig;
import com.dojoconsulting.gigawatt.core.BackTestToolException;
import com.dojoconsulting.gigawatt.core.IMarketManager;
import com.dojoconsulting.gigawatt.data.IMarketData;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 13-Oct-2007
 * Time: 02:06:38
 */
public class FXMarketManager implements IMarketManager {

    private final Map<FXPair, IMarketData> markets;
    private final Map<FXPair, FXTick> tickTable;
    private final Map<FXPair, List<FXTick>> perLoopTickTable;
    private boolean newTicksThisLoop;

    public FXMarketManager() {
        markets = new HashMap<FXPair, IMarketData>();
        tickTable = new HashMap<FXPair, FXTick>();
        perLoopTickTable = new HashMap<FXPair, List<FXTick>>();
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
            perLoopTickTable.put((FXPair) marketData.getProduct(), new ArrayList<FXTick>());
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

    public Map<FXPair, List<FXTick>> getPerLoopTickTable() {
        return perLoopTickTable;
    }

    public void registerTick(final FXPair pair, final FXTick tick) {
        final List<FXTick> list = perLoopTickTable.get(pair);
        list.add(tick);
        newTicksThisLoop = true;
    }

    public void nextTick(final long currentTimeInMillis) {
        newTicksThisLoop = false;
        final Collection<List<FXTick>> loopValues = perLoopTickTable.values();
        for (final List<FXTick> list : loopValues) {
            list.clear();
        }
        final Collection<IMarketData> marketValues = markets.values();
        for (final IMarketData marketData : marketValues) {
            final FXTick tick = (FXTick) marketData.getNextTick(currentTimeInMillis);
            if (tick != null && tick.getTimestamp() != 0) {
                tickTable.put((FXPair) marketData.getProduct(), tick);
            }
        }
    }
}
