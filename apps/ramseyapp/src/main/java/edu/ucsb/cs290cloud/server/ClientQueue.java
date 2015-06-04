package edu.ucsb.cs290cloud.server;

import java.net.SocketAddress;
import java.util.LinkedList;

public class ClientQueue extends Thread {
	class ClientInfo {
		SocketAddress clientSocketAddress;
		long lastUpdate;
		Boolean isStuck = false;
		Class strategyClass;
	}
	
	public final int CLIENT_TIMEOUT = 120000; // 2mn TIMEOUT 
	
	private LinkedList<ClientInfo> clientList;
	
	
	public void run() {
		this.checkPresenceOfClients();
	}
	
	public synchronized void checkPresenceOfClients() {
		ClientInfo client;
		long currentTime;
		
		currentTime = System.currentTimeMillis();
		
		for (int i = 0; i < this.clientList.size(); i++) {
			client = this.clientList.get(i);
			if (client.lastUpdate < (currentTime - this.CLIENT_TIMEOUT)) {
				
			}
		}
	}

}
