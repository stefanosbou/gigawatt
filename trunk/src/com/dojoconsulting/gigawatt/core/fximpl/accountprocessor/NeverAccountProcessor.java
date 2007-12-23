package com.dojoconsulting.gigawatt.core.fximpl.accountprocessor;

/**
 * Created by IntelliJ IDEA.
 * User: Amit
 * Date: 19-Dec-2007
 * Time: 18:38:20
 */
public class NeverAccountProcessor implements IAccountProcessorStrategy {

	public boolean requiresMarginCall() {
		return false;
	}
}