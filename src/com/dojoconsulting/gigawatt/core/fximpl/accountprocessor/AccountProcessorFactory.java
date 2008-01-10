package com.dojoconsulting.gigawatt.core.fximpl.accountprocessor;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 19-Dec-2007
 * Time: 18:59:07
 */
public class AccountProcessorFactory {

	private static BeanFactory beanFactory;

	static {
		final BeanFactoryLocator bfLocator = SingletonBeanFactoryLocator.getInstance("com/dojoconsulting/gigawatt/config/beanRefContext.xml");
		final BeanFactoryReference bf = bfLocator.useBeanFactory("fxOandaEngineFactory");
		beanFactory = bf.getFactory();
	}

	public static IAccountProcessorStrategy getProcessor(final String processType) {
		if (processType != null) {
			if (processType.equalsIgnoreCase("NEVER")) {
				return (IAccountProcessorStrategy) beanFactory.getBean("neverAccountProcessor");
			}
			if (processType.equalsIgnoreCase("DAILY")) {
				return (IAccountProcessorStrategy) beanFactory.getBean("dailyAccountProcessor");
			}
		}
		return (IAccountProcessorStrategy) beanFactory.getBean("fullAccountProcessor");
	}
}
