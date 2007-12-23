package com.dojoconsulting.gigawatt.core.fximpl.domain;

import com.dojoconsulting.oanda.fxtrade.api.FXPair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 02-Dec-2007
 * Time: 00:54:51
 */
public class FXPairFlyweightFactory {
	private Map<String, FXPair> factory = new HashMap<String, FXPair>();

	private static final FXPairFlyweightFactory instance = new FXPairFlyweightFactory();

	public static FXPairFlyweightFactory getInstance() {
		return instance;
	}

	private FXPairFlyweightFactory() {

	}


	public FXPair getPair(final String base, final String quote) {
		return getPair(base + "/" + quote);
	}

	private FXPair getPair(final String pair) {
		FXPair fxPair = factory.get(pair);
		if (fxPair == null) {
			fxPair = new FXPair(pair);
			factory.put(pair, fxPair);
		}
		return fxPair;
	}
}
