package edu.ucsb.cs290cloud.client;

import java.io.*;
import java.net.*;

import edu.ucsb.cs290cloud.commons.Message;

public class ClientMessenger implements Runnable {

	private DatagramSocket clientSocket;
	private DatagramPacket receivePacket;
	private Message messageToServer, messageFromServer;
	private byte[] receivedBytes, bytesToSend;
	private InetAddress serverIP;
	private int serverPort;

	public ClientMessenger(Message msg, DatagramSocket socket, InetAddress ip,
			int port) {
		this.messageToServer = msg;
		this.clientSocket = socket;
		this.serverIP = ip;
		this.serverPort = port;
	}

	public void run() {
		while (true) {
			try {
				// get the new message each iteration
				this.messageToServer = Client.msgToServer;
				
				// send the message to the server
				bytesToSend = messageToServer.serialize();
				DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
						bytesToSend.length, serverIP, serverPort);
				clientSocket.send(sendPacket);

				// receive response from server
				receivePacket = new DatagramPacket(receivedBytes,
						receivedBytes.length);
				receivedBytes = receivePacket.getData();
				messageFromServer = Message.deserialize(receivedBytes);
				Client.currentMsgFromServer = messageFromServer;
				System.out.println("From Server: "
						+ messageFromServer.getMessage());
				
				// mark message as unread
				Client.isStale = false;
				
				// wait for 30s before doing it again (arbitrary)
				Thread.sleep(30000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
