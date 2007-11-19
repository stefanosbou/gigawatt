package com.dojoconsulting.gigawatt.config;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 23-Oct-2007
 * Time: 03:08:08
 * To change this template use File | Settings | File Templates.
 */
public class UserConfig {
    private int id;
    private String username;
    private String password;
    private String name;
    private String address;
    private String email;
    private String telephone;
    private Date createdate;
    private List<AccountConfig> accounts;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(final String telephone) {
        this.telephone = telephone;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(final Date createdate) {
        this.createdate = createdate;
    }

    public List<AccountConfig> getAccounts() {
        return accounts;
    }

    public void setAccounts(final List<AccountConfig> accounts) {
        this.accounts = accounts;
    }
}
