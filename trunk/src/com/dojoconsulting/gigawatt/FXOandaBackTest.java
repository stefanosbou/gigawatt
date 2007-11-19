package com.dojoconsulting.gigawatt;

import com.dojoconsulting.gigawatt.config.BackTestConfig;
import com.dojoconsulting.gigawatt.core.Engine;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;

import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 15-Oct-2007
 * Time: 05:25:37
 * To change this template use File | Settings | File Templates.
 */
public class FXOandaBackTest {

    public static void main(final String[] args) throws FileNotFoundException {

        if (args.length == 0) {
            System.out.println("Please specify backtest config XML file");
            System.exit(0);
        }
        System.setProperty(BackTestConfig.CONFIG_PROPERTY, args[0]);
        final BeanFactoryLocator bfLocator = ContextSingletonBeanFactoryLocator.getInstance();
        final BeanFactoryReference bfReference = bfLocator.useBeanFactory("fxOandaEngineFactory");
        final BeanFactory factory = bfReference.getFactory();

        final Engine engine = (Engine) factory.getBean("engine");
        engine.start();
    }
}
