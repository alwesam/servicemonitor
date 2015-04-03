package com.vandevsam.service.test;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.HashMap;

import org.junit.Test;

import com.vandevsam.service.ServiceMonitor;

public class TestConnections {

	private static final String localHost = "localhost";
	private static final int httpPort = 80;
	private static final int mysqlPort = 3306;
	private static int mysqlPortBad = 3305; // intentionally wrong
	private static int callerID = 1;
	private static int frequency = 6;

	@Test
	public void testConn() {

		ServiceMonitor service = new ServiceMonitor(localHost, httpPort,
				frequency);
		new Thread(service, "localhost").start();

		// wait
		waitup();

		ServiceMonitor.registerCaller(localHost, httpPort, callerID);

		// calling mysql port which was intentionally turned off
		ServiceMonitor service2 = new ServiceMonitor(localHost, mysqlPortBad,
				frequency);
		new Thread(service2, "mysql").start();

		waitup();

		// this will return false since the service never connected and the
		// callers
		// was not instantiated as a result
		assertFalse(ServiceMonitor.registerCaller(localHost, mysqlPortBad,
				callerID));

		HashMap<Socket, Boolean> list = ServiceMonitor.notifyCaller(callerID);

		for (Socket s : list.keySet()) {
			if (s.getPort() == httpPort)
				assertTrue(list.get(s));
			if (s.getPort() == mysqlPortBad)
				assertFalse(list.get(s));
		}

		// now fix this port
		service2.setPort(mysqlPort);

		assertEquals(mysqlPort, service2.getPort());

		waitup();

		list = ServiceMonitor.notifyCaller(callerID);

		for (Socket s : list.keySet()) {
			if (s.getPort() == httpPort)
				assertTrue(list.get(s));
			if (s.getPort() == mysqlPort)
				assertTrue(list.get(s));
		}

	}

	private void waitup() {
		try {
			Thread.sleep(frequency * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
