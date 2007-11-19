/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 17-Oct-2007
 * Time: 03:45:28
 * To change this template use File | Settings | File Templates.
 */
package com.dojoconsulting.gigawatt.core;

import com.dojoconsulting.gigawatt.config.BackTestConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServer {
    private static TimeServer instance = new TimeServer();
    private static int TIME_INCREMENT;
    private String startDateAsString;
    private Date startDate;

    public static TimeServer getInstance() {
        return instance;
    }

    private TimeServer() {
    }

    private long currentTimeInMillis;

    void init(final BackTestConfig config) {
        startDate = config.getStartdate();
        startDateAsString = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(startDate);
        currentTimeInMillis = startDate.getTime();
        TIME_INCREMENT = config.getIncrement();
    }

    public long getTime() {
        return currentTimeInMillis;
    }

    public long processNextLoop() {
        currentTimeInMillis += TIME_INCREMENT;
        return currentTimeInMillis;
    }

    public String getStartDateAsString() {
        return startDateAsString;
    }

}
