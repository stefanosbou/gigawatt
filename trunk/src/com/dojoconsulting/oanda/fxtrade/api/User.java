package com.dojoconsulting.oanda.fxtrade.api;

import com.dojoconsulting.gigawatt.core.IUser;

import java.util.Vector;

/**
 * Provides access to user information.
 */
public final class User implements IUser {
    private Vector<Account> accounts;
    private String address;
    private long createDate;
    private String email;
    private String name;
    private String password;
    private String profile;
    private String telephone;
    private int userId;
    private String userName;

    User(final int userId, final String userName, final String password, final String name, final String address, final String telephone, final String email, final long createDate) {
        this.address = address;
        this.createDate = createDate;
        this.email = email;
        this.name = name;
        this.password = password;
        this.telephone = telephone;
        this.userId = userId;
        this.userName = userName;
    }

    public Vector<Account> getAccounts() {
        return accounts;
    }

    void setAccounts(final Vector<Account> accounts) {
        this.accounts = accounts;
    }

    public void setProfile(final String profile) throws UserException {
        this.profile = profile;
    }

    public Account getAccountWithId(final int accountId) throws AccountException {
        for (final Account acc : accounts) {
            if (acc.getAccountId() == (accountId)) {
                return acc;
            }
        }
        throw new AccountException("Account " + accountId + " does not belong to this user");
    }

    public String getAddress() {
        return address;
    }

    public long getCreateDate() {
        return createDate;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getProfile() {
        return profile;
    }

    public String getTelephone() {
        return telephone;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
