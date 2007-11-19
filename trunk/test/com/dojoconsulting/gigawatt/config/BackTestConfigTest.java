package com.dojoconsulting.gigawatt.config;

import junit.framework.TestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Amit Chada
 * Date: 23-Oct-2007
 * Time: 03:24:31
 * To change this template use File | Settings | File Templates.
 */
public class BackTestConfigTest extends TestCase {

	public void testLoad() throws ParseException {
		System.setProperty(BackTestConfig.CONFIG_PROPERTY, "C:\\projects\\IdeaProjects\\gigawatt\\test\\com\\dojoconsulting\\gigawatt\\config\\testconfig.xml");
		final BackTestConfig config = BackTestConfig.load();
		assertEquals("Increment was not set correctly.", 1000, config.getIncrement());
		assertEquals("Start date was not set correctly", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2006-12-30 01:21:25").getTime(), config.getStartdate().getTime());
		assertEquals("Engine was not set correctly.", "fxoanda", config.getEngine());

		final List<UserConfig> users = config.getUsers();
		assertEquals("Incorrect number of users.", 1, users.size());

		final UserConfig user = users.get(0);
		assertEquals("User Id was not set correctly.", 12345678, user.getId());
		assertEquals("User UserName was not set correctly.", "SomeUserName", user.getUsername());
		assertEquals("User password was not set correctly.", "somepassword", user.getPassword());
		assertEquals("User name was not set correctly.", "Amit Chada", user.getName());
		assertEquals("User address was not set correctly.", "1 Here Street, OverThere Town, OverHere City, AB1 2BC, UK", user.getAddress());
		assertEquals("User email was not set correctly.", "amit@amitchada.com", user.getEmail());
		assertEquals("User create Date was not set correctly", new SimpleDateFormat("yyyy-MM-dd").parse("2007-01-01").getTime(), user.getCreatedate().getTime());
		assertEquals("User telephone was not set correctly.", "+44 555 555 5555", user.getTelephone());

		final List<AccountConfig> accounts = user.getAccounts();
		assertEquals("Incorrect number of accounts.", 2, accounts.size());

	}

}
