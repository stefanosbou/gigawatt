/**
 * 
 */
package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;

/**
 * @author Nick Skaggs
 *
 */
public interface IInterestRateManager {

	void close();

	void init(BackTestConfig config);

}
