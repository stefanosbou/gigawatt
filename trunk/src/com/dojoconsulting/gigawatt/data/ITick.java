package com.dojoconsulting.gigawatt.data;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 22-Oct-2007
 * Time: 22:53:41
 */
public interface ITick {
	long getTimestamp();

	double getBid();

	double getAsk();

}
