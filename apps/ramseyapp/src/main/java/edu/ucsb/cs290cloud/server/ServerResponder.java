package edu.ucsb.cs290cloud.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

import edu.ucsb.cs290cloud.networkcommunication.Message;

public class ServerResponder implements Runnable {
	
	private Scheduler scheduler;
	private DatagramSocket serverSocket;
	private DatagramPacket packet;
	
	public ServerResponder(DatagramSocket serverSocket, DatagramPacket packet, 
			Scheduler scheduler) {
		this.serverSocket = serverSocket;
		this.packet = packet;
		this.scheduler = scheduler;
	}
	
	public void run() {
		byte[] receivedBytes, bytesToSend;
		Message messageFromClient, messageForClient;
		
		receivedBytes = this.packet.getData();
		messageFromClient = Message.deserialize(receivedBytes);
		
		messageForClient = null; // TO BE REMOVED
//		messageForClient = makeMessage();
		bytesToSend = messageForClient.serialize();
		this.sendMessage(bytesToSend);
	}
	
	public void sendMessage(byte[] bytesToSend) {
		DatagramPacket responsePacket;
		
		responsePacket = new DatagramPacket(bytesToSend, bytesToSend.length, 
	    		this.packet.getAddress(), this.packet.getPort());
	    
	    try {
			this.serverSocket.send(responsePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
