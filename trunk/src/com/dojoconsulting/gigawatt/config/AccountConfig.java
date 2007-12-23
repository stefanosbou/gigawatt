package com.dojoconsulting.gigawatt.config;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 23-Oct-2007
 * Time: 03:09:20
 */
public class AccountConfig {
	private int id;
	private String name;
	private double balance;
	private Date createdate;
	private String currency;
	private int leverage;
	private String processType = "";

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(final String processType) {
		this.processType = processType;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(final double balance) {
		this.balance = balance;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(final Date createdate) {
		this.createdate = createdate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(final String currency) {
		this.currency = currency;
	}

	public int getLeverage() {
		return leverage;
	}

	public void setLeverage(final int leverage) {
		this.leverage = leverage;
	}
}
