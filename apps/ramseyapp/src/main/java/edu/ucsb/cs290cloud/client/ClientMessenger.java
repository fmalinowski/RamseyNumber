package edu.ucsb.cs290cloud.client;

import java.io.*;
import java.net.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import edu.ucsb.cs290cloud.networkcommunication.Message;

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

				// send the message
				if (messageToServer.getMessage() == "READY") {
					;
				} else if (messageToServer.getMessage() == "COUNTEREXAMPLE") {
					;
				} else if (messageToServer.getMessage() == "STATUS") {
					;
				}
				bytesToSend = messageToServer.serialize();
				DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
						bytesToSend.length, serverIP, serverPort);
				clientSocket.send(sendPacket);

				// receive response
				receivePacket = new DatagramPacket(receivedBytes,
						receivedBytes.length);
				receivedBytes = receivePacket.getData();
				messageFromServer = Message.deserialize(receivedBytes);
				Client.currentMsgFromServer = messageFromServer;
				System.out.println("From Server: "
						+ messageFromServer.getMessage());
				// wait for 30s
				Thread.sleep(30000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
