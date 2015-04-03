package com.vandevsam.service;

public class ServiceMonitorMain {
	
	private static final String google = "goole.com";
	private static final String myWeb = "vandevsam.com";
	private static final String localHost = "localhost";
	
	private static final int httpPort = 80;
	private static final int mysqlPort = 3306;
	
	public static void main(String[] args) {
		
		//creates a bunch of callers who will be interested
		ServiceMonitor service1 = new ServiceMonitor(myWeb, httpPort,5);		
		new Thread(service1,"remotehost").start();		
		ServiceMonitor service2 = new ServiceMonitor(localHost,mysqlPort,5);		
		new Thread(service2,"localhost").start();		
		ServiceMonitor service3 = new ServiceMonitor(google,httpPort,10);		
		new Thread(service3,"google").start();
		
		int i = 1;
		while (true){
			try {
				Thread.sleep(10*1000);			
				System.out.println("---------------");
				System.out.println("Main Thread here:  "+i);
				ServiceMonitor.notifyCaller(8);
				ServiceMonitor.registerCaller("localhost",80,i++);
				
				System.out.println("---------------");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	catch (NullPointerException e){
				
			}
		} 
		
	} 
	

}
