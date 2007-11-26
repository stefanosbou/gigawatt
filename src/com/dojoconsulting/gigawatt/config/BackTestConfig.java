package com.dojoconsulting.gigawatt.config;

import com.dojoconsulting.gigawatt.core.GigawattException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 23-Oct-2007
 * Time: 03:04:19
 */
public class BackTestConfig {
	private Date startdate;
	private String engine;
	private int increment;
	private List<UserConfig> users;
	private List<String> strategies;
	private List<MarketConfig> markets;
	public static final String CONFIG_PROPERTY = "com.dojoconsulting.gigawatt.config";

	public static BackTestConfig load() {
		final String xmlFile = System.getProperty(CONFIG_PROPERTY);
		try {

			final StringBuffer xml = new StringBuffer();
			final BufferedReader in = new BufferedReader(new FileReader(xmlFile));
			while (in.ready()) {
				xml.append(in.readLine());
			}

			final XStream xstream = new XStream();
			xstream.alias("backtestconfig", BackTestConfig.class);
			xstream.alias("account", AccountConfig.class);
			xstream.alias("market", MarketConfig.class);
			xstream.alias("user", UserConfig.class);
			xstream.alias("strategy", String.class);
			xstream.useAttributeFor("engine", String.class);
			xstream.useAttributeFor("id", Integer.class);
			xstream.useAttributeFor("product", String.class);
			xstream.useAttributeFor("keepHistory", Boolean.class);

			xstream.registerConverter(new DateConverter("yyyy-MM-dd hh:mm:ss", new String[]{"yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd"}));
			return (BackTestConfig) xstream.fromXML(xml.toString());
		}
		catch (FileNotFoundException e) {
			throw new GigawattException("BackTestConfig: There was a problem finding " + xmlFile + ".  Please verify filename is correct.", e);
		}
		catch (IOException e) {
			throw new GigawattException("BackTestConfig: There was a problem reading " + xmlFile + ".  Please verify filename is correct and not locked by another process.", e);
		}
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(final Date startdate) {
		this.startdate = startdate;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(final int increment) {
		this.increment = increment;
	}

	public List<UserConfig> getUsers() {
		return users;
	}

	public void setUsers(final List<UserConfig> users) {
		this.users = users;
	}

	public List<String> getStrategies() {
		return strategies;
	}

	public void setStrategies(final List<String> strategies) {
		this.strategies = strategies;
	}

	public List<MarketConfig> getMarkets() {
		return markets;
	}

	public void setMarkets(final List<MarketConfig> markets) {
		this.markets = markets;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(final String engine) {
		this.engine = engine;
	}
}
