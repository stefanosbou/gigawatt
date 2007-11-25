package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;

/**
 * User: Amit Chada
 * Date: 22-Oct-2007
 * Time: 23:00:12
 */
public interface IEngineProcess {
	void init(final BackTestConfig config);

	void preTickProcess();

	void postTickProcess();

	void close();
}
