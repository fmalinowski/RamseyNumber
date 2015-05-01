package edu.ucsb.cs290cloud.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable {
	
	private static final int BUFFER_SIZE = 65507;
	
	private DatagramSocket serverSocket;
	private Boolean shouldStopServer = false;
	private Scheduler scheduler;
	
	public Server(int port, Scheduler scheduler) {
		this.scheduler = scheduler;
		
		try {
			this.serverSocket = new DatagramSocket(port);
			System.out.println("Waiting for clients on port " + 
					this.serverSocket.getLocalPort() + "...");
		} catch (SocketException e) {
			System.err.println("ERROR: impossible to create a Datagram Socket");
			e.printStackTrace();
		}
	}
	
	public void halt() {
		this.shouldStopServer = true;
	}
	
	public void run() {
		while (true) {
			DatagramPacket packet;
			byte[] buffer;
			
			if (this.shouldStopServer) {
				return;
			}
			
			buffer = new byte[BUFFER_SIZE];
			packet = new DatagramPacket(buffer, buffer.length);
			
			System.out.println("WAITING FOR PACKET");
			
			try {
				this.serverSocket.receive(packet);
				System.out.println("udp Packet Received!");
				new Thread(new ServerResponder(this.serverSocket, packet, this.scheduler)).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}

}
