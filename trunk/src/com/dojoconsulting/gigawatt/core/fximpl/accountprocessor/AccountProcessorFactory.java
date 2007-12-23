package com.dojoconsulting.gigawatt.core.fximpl.accountprocessor;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 19-Dec-2007
 * Time: 18:59:07
 */
public class AccountProcessorFactory {


	public static IAccountProcessorStrategy getProcessor(final String processType) {
		if (processType.equals("NEVER")) {
			return new NeverAccountProcessor();
		}
		if (processType.equals("MINIMAL")) {
			return new OnceADayAccountProcessor();
		}
		return new FullAccountProcessor();
	}
}
