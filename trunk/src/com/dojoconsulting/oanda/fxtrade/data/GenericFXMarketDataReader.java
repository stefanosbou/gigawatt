package com.dojoconsulting.oanda.fxtrade.data;

import com.dojoconsulting.gigawatt.core.BackTestToolException;
import com.dojoconsulting.gigawatt.core.IMarketManager;
import com.dojoconsulting.gigawatt.core.fximpl.FXMarketManager;
import com.dojoconsulting.gigawatt.data.IMarketData;
import com.dojoconsulting.gigawatt.data.ITick;
import com.dojoconsulting.oanda.fxtrade.api.FXPair;
import com.dojoconsulting.oanda.fxtrade.api.FXTick;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 27-Oct-2007
 * Time: 20:25:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class GenericFXMarketDataReader implements IMarketData {
    protected final FXPair pair;
    protected BufferedReader in;
    private FXTick lastTick = new FXTick(0, 0.0, 0.0);
    private FXTick nextPossibleTick = new FXTick(0, 0.0, 0.0);

    private DateFormat formatter;

    private FXMarketManager marketManager;

    public void setMarketManager(final IMarketManager marketManager) {
        this.marketManager = (FXMarketManager) marketManager;
    }

    public GenericFXMarketDataReader(final FXPair pair, final String path) {
        this.pair = pair;
        try {
            in = new BufferedReader(new FileReader(path));
        }
        catch (FileNotFoundException e) {
            throw new BackTestToolException("GenericFXMarketDataReader: Could not find the file (" + path + ") for " + pair, e);
        }
        formatter = getDateFormatter();
    }

    public boolean hasMoreTicks() {
        try {
            return in.ready() || nextPossibleTick != null;
        }
        catch (IOException e) {
            throw new BackTestToolException("GenericFXMarketDataReader: There was a problem reading market data for " + pair, e);
        }
    }

    public ITick getNextTick(final long currentTimeInMillis) {
        String currentToken = null;
        try {
            if (nextPossibleTick != null) {
                if (nextPossibleTick.getTimestamp() > currentTimeInMillis) {
                    return null;
                }
                lastTick = nextPossibleTick;
                marketManager.registerTick(pair, lastTick);
                nextPossibleTick = null;
            }
            if (!in.ready()) {
                return null;
            }
            while (in.ready()) {
                final String dataRecord = in.readLine();
                if (dataRecord == null) {
                    return null;
                }
                final String[] tokens = processRecord(dataRecord);
                currentToken = tokens[0];
                final Date date = formatter.parse(currentToken);
                final long millis = date.getTime();
                currentToken = tokens[1];
                final double bid = Double.parseDouble(currentToken);
                currentToken = tokens[2];
                final double ask = Double.parseDouble(currentToken);
                if (millis > currentTimeInMillis) {
                    nextPossibleTick = new FXTick(millis, bid, ask);
                    return lastTick;
                }
                lastTick = new FXTick(millis, bid, ask);
                marketManager.registerTick(pair, lastTick);
            }
            return lastTick;
        }
        catch (IOException e) {
            throw new BackTestToolException("GenericFXMarketDataReader: There was a problem reading market data for " + pair, e);
        }
        catch (ParseException e) {
            throw new BackTestToolException("GenericFXMarketDataReader: There was a problem parsing the date in " + pair + " with the following data: '" + currentToken + "'.", e);
        }
    }

    public FXPair getProduct() {
        return pair;
    }

    protected abstract String[] processRecord(final String record);

    protected abstract DateFormat getDateFormatter();

}
