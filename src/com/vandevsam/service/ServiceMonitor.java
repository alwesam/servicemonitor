//Wesam Al-Haddad
//April 2015

package com.vandevsam.service;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class ServiceMonitor implements Runnable {
		
	//fields attached to the monitor class
	private static HashMap<Socket, ArrayList<Integer>> sockets = new HashMap<>(); //a hashmap of service sockets and callers	
	private static HashMap<Socket, Boolean> socketsStatus = new HashMap<>(); //a hashmap of service sockets and its status	
	//status messages
	private static final String SERVICE_UP = "Service is UP";
	private static final String SERVICE_DOWN = "Service is DOWN";
	
	//fields attached to the service object
	private int frequency;
	private Socket currSocket;
	private String host;
	private int port;
	
	//the following fields can be grouped together
	//and moved to a new class called ServiceObj where
	//the fields associated with each service can be
	//created and managed away from the service monitor.
	//too bad it was too late when I realized this
	public int getFrequency(){
		return this.frequency;
	}
	public int getPort(){
		return this.port;
	}
	public String getHost(){
		return this.host;
	}
	public Socket getSocket(){
		return this.currSocket;
	}
	
	public void setFrequency(int f){
		if (f>=1)
			this.frequency = f;
		else
			this.frequency = 1;
	}
	
	public void setHost(String h){
		this.host = h;
	}
	public void setPort (int p){
		this.port = p;
	}
	
	//constructor instantiates a service with host, port, and
	//polling frequency
	public ServiceMonitor(String host, int port, int polling){		
		this.host = host;
		this.port = port;
		this.currSocket = new Socket();
		if(polling>=1) //check if frequency will be equal or greate than one second
			this.frequency = polling;
		else
			this.frequency = 1;	
	}
	
	//only one thread will check create a socket at once
	private synchronized boolean tcpCheck(){	
		
		System.out.println("Checking: "+this.currSocket);
		
		boolean connected=false;	
				
		try(
				Socket s = new Socket(this.host, this.port);
			){
					//check previous value and see if it was connected
					if(!this.currSocket.isConnected()){
						//System.out.println("It was unconnected");
						this.currSocket = s;
						sockets.put(this.currSocket, new ArrayList<Integer>());
						socketsStatus.put(this.currSocket,true);
					}
					
					connected = true;
            	
            }catch (UnknownHostException e) {
                System.err.println("Don't know about host ");
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to host ");
                //e.printStackTrace();
            } 
		
		return connected;	
	}
	
	//returns true if caller is successfully registered to be notified of service status
	//returns false if the requested service is not found or was never connected or the caller 
	//has already been registered or the host/port combo is incorrect
	public static boolean registerCaller(String host, int port, int callerID){
			//s.getInetAddress().toString().replaceAll("/.*", "").equals(host)
		for (Socket s: sockets.keySet()){
			if(s.getPort() == port &&
			   (s.getInetAddress().getHostName().equals(host) || //in case it's a domain name
			    s.getInetAddress().getHostAddress().equals(host))) //in case it's an ip address
			{
				//add caller to the service
				//check if callerID hasn't been added before
				int numCallers = sockets.get(s).size();
				int found = 0;
				if(numCallers==0) {sockets.get(s).add(callerID); return true;}
				else {
					for (int i=0; i<numCallers;i++){
						if(sockets.get(s).get(i)!= callerID)
							found = 1;
					}
					if(found==1){
						sockets.get(s).add(callerID);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	//searches through the sockets and sockets status hashmpas and
	//returns a hashmap of sockets and their connection statuses as
	//registered by the caller
	public static HashMap<Socket,Boolean> notifyCaller(int callerID){
		HashMap<Socket,Boolean> statuses = new HashMap<>();
		for(Socket s: socketsStatus.keySet()){
			if(sockets.get(s)!=null){
				for (int i=0; i<sockets.get(s).size();i++){				
					if(sockets.get(s).get(i)==callerID)
						statuses.put(s, socketsStatus.get(s));
				}
			}
		}
		return statuses;
	}

	//implements the run method
	@Override
	public void run() {
		while(true){				
			
			System.out.println("Thread: "+Thread.currentThread().getName());
			
			if(tcpCheck()){										
				System.out.println(SERVICE_UP);
				//socketsStatus.put(this.currSocket,true);
			}
			else{
				System.out.println(SERVICE_DOWN);
				socketsStatus.put(this.currSocket,false);				
			}
			
			System.out.println("-------------------------");	
			
			//wait
			try {
				Thread.sleep(this.frequency*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

}
