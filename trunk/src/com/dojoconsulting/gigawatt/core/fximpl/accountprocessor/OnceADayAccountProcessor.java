package com.dojoconsulting.gigawatt.core.fximpl.accountprocessor;

import com.dojoconsulting.gigawatt.core.TimeServer;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 19-Dec-2007
 * Time: 18:40:04
 */
public class OnceADayAccountProcessor implements IAccountProcessorStrategy {

	private static final TimeServer timeServer = TimeServer.getInstance();
	private static final int SECS_IN_A_DAY = 1000 * 60 * 60 * 24;

	public boolean requiresMarginCall() {
		final long time = timeServer.getTimeSinceStart();
		return time % SECS_IN_A_DAY == 0;
	}
}
