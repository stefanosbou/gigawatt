package com.dojoconsulting.gigawatt;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.Engine;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;

import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 05:25:37
 */
public class FXOandaBackTest {

	public static void main(final String[] args) throws FileNotFoundException {

		if (args.length == 0) {
			System.out.println("Please specify backtest config XML file");
			System.exit(0);
		}
		System.setProperty(BackTestConfig.CONFIG_PROPERTY, args[0]);
		final BeanFactoryLocator bfLocator = SingletonBeanFactoryLocator.getInstance("com/dojoconsulting/gigawatt/config/beanRefContext.xml");
		final BeanFactoryReference bfReference = bfLocator.useBeanFactory("fxOandaEngineFactory");
		final BeanFactory factory = bfReference.getFactory();

		final Engine engine = (Engine) factory.getBean("engine");
		engine.start();
	}
}
