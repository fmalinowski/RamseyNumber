package edu.ucsb.cs290cloud.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable {
	static Logger LOGGER = LoggerFactory.getLogger(Server.class);

	private static final int BUFFER_SIZE = 65507;
	
	private DatagramSocket serverSocket;
	private Boolean shouldStopServer = false;
	private Scheduler scheduler;
	
	public Server(int port, Scheduler scheduler) {
		this.scheduler = scheduler;
		
		try {
			this.serverSocket = new DatagramSocket(port);
			LOGGER.debug("Waiting for clients on port " +
					this.serverSocket.getLocalPort() + "...");
		} catch (SocketException e) {
			LOGGER.error("ERROR: impossible to create a Datagram Socket");
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

			LOGGER.debug("WAITING FOR PACKET");

			try {
				this.serverSocket.receive(packet);
				LOGGER.debug("udp Packet Received");
				new Thread(new ServerResponder(this.serverSocket, packet, this.scheduler)).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}

}
