package com.dojoconsulting.gigawatt.core;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 18-Oct-2007
 * Time: 14:28:58
 * To change this template use File | Settings | File Templates.
 */
public interface IUserManager extends IEngineProcess {
    IUser getUser(String userName);

    boolean verifyLogin(String userName, String password);
}
