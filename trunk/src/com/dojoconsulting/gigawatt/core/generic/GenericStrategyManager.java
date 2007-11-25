package com.dojoconsulting.gigawatt.core.generic;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.BackTestToolException;
import com.dojoconsulting.gigawatt.core.IStrategyManager;
import com.dojoconsulting.gigawatt.strategy.IStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 22-Oct-2007
 * Time: 22:57:46
 * To change this template use File | Settings | File Templates.
 */
public class GenericStrategyManager implements IStrategyManager {
	private List<IStrategy> strategies;


	public void init(final BackTestConfig config) {
		strategies = new ArrayList<IStrategy>();
		final List<String> strategyClassNames = config.getStrategies();
		String classNameHack = null;
		try {
			for (final String className : strategyClassNames) {
				classNameHack = className;
				final Class strategyClass = Class.forName(className);
				final IStrategy strategy = (IStrategy) strategyClass.newInstance();
				strategies.add(strategy);
			}
		}
		catch (ClassNotFoundException e) {
			throw new BackTestToolException("GenericStrategyManager: There was a problem finding " + classNameHack + ".  Please ensure you have placed it on the classpath.", e);
		}
		catch (InstantiationException e) {
			throw new BackTestToolException("GenericStrategyManager: There was a problem instantiating " + classNameHack + ".  Please ensure you have a public no-arg constructor.", e);
		}
		catch (IllegalAccessException e) {
			throw new BackTestToolException("GenericStrategyManager: There was a problem instantiating " + classNameHack + ".  Please ensure you have a public no-arg constructor.", e);
		}

		for (final IStrategy strategy : strategies) {
			strategy.init();
		}
	}

	public void preTickProcess() {
		// nothing to do preTick
	}

	public void postTickProcess() {
		for (final IStrategy strategy : strategies) {
			strategy.handle();
		}
	}

	public void close() {
		//Nothing to do.
	}
}
