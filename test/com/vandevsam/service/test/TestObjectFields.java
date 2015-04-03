package com.vandevsam.service.test;

import static org.junit.Assert.*;

import java.net.Socket;

import org.junit.Test;

import com.vandevsam.service.ServiceMonitor;

public class TestObjectFields {

	private static final String google = "goole.com";
	private static final String myWeb = "vandevsam.com";
	private static final String localHost = "localhost";

	private static final int httpPort = 80;
	private static final int mysqlPort = 3306;

	@Test
	public void testFieldObjs() {
		ServiceMonitor test1 = new ServiceMonitor(myWeb, httpPort, 5);
		assertEquals(5, test1.getFrequency());
		assertEquals(80, test1.getPort());

		ServiceMonitor test2 = new ServiceMonitor(google, httpPort, 0);
		// frequency should be at least 1 second
		assertEquals(1, test2.getFrequency());
		assertEquals(google, test2.getHost());

		// should return
		assertEquals(myWeb, test1.getHost());

		ServiceMonitor test3 = new ServiceMonitor(localHost, mysqlPort, 10);
		assertEquals(mysqlPort, test3.getPort());
		// should return localhost
		assertEquals(localHost, test3.getHost());

		// testsockets
		// should return unconnected
		Socket s = new Socket();
		assertEquals(s.toString(), test1.getSocket().toString());
		assertEquals(s.toString(), test2.getSocket().toString());
		assertEquals(s.toString(), test3.getSocket().toString());

	}

}
