package com.vandevsam.service.test;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.HashMap;

import org.junit.Test;

import com.vandevsam.service.ServiceMonitor;


public class TestCallerRequests {
	
	private static final String localHost = "localhost";
	private static final String remoteHost = "vandevsam.com";
	private static final int httpPort = 80;
	private static int callerID = 1;
	private static int frequency = 6;

	@Test
	public void testCallRequests() {
		
		ServiceMonitor service = new ServiceMonitor(localHost, httpPort, frequency);		
		
		boolean result = ServiceMonitor.registerCaller(localHost, httpPort, callerID);		
		//should return false since the service hasn't started yet and hence it's not
		//in the list of services available for monitoring by callers
		assertFalse(result);				
		new Thread(service,"localhost").start();
		
		waitup();
		
		//allow for time until the service is established before requesting to monitor
		//the service
		result = ServiceMonitor.registerCaller(localHost, httpPort, callerID);		
		//should return true
		assertTrue(result);
		
		//start a new service thread
		ServiceMonitor service2 = new ServiceMonitor(remoteHost, httpPort, frequency);
		new Thread(service2,"remotehost").start();
		
		waitup();
		
		assertTrue(ServiceMonitor.registerCaller(remoteHost, httpPort, callerID));		
		//should return false, since this caller has already requested successfully this service
		assertFalse(ServiceMonitor.registerCaller(localHost, httpPort, callerID));		
		//should return a list of 
		HashMap<Socket,Boolean> list = ServiceMonitor.notifyCaller(callerID);		
		//should not be null
		assertNotNull(list);		
		//should return 2 since we added two services
		assertEquals(2, list.size());
		
	}
	
	private void waitup(){
		//wait
			try {
				Thread.sleep(frequency*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
