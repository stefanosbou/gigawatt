package com.dojoconsulting.gigawatt.core.fximpl.accountprocessor;

import com.dojoconsulting.gigawatt.core.TimeServer;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 19-Dec-2007
 * Time: 18:40:04
 */
public class DailyAccountProcessor implements IAccountProcessorStrategy {

	private static final int SECS_IN_A_DAY = 1000 * 60 * 60 * 24;
	private TimeServer timeServer;

	public boolean requiresMarginCall() {
		final long time = timeServer.getTimeSinceStart();
		return time % SECS_IN_A_DAY == 0;
	}

	public void setTimeServer(final TimeServer timeServer) {
		this.timeServer = timeServer;
	}
}
