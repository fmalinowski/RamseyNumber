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
	private String instruction;
	private Message messageToServer, messageFromServer;
	private byte[] receivedBytes, bytesToSend;

	public ClientMessenger(String msg) {
		this.instruction = msg;
	}

	public void run() {
		try {
			InetAddress IPAddress = InetAddress.getByName("localhost");
			clientSocket = new DatagramSocket();
			
			// send the message
			messageToServer.setMessage(instruction);
			if(instruction == "READY") {
				
			}
			else if(instruction == "COUNTEREXAMPLE") {
				messageToServer.setBestCount(bestCount);
				messageToServer.setBestCountTime(bestCountTime);
				messageToServer.setGraph(graph);
				messageToServer.setStrategy(strategy);
				messageToServer.setNbTimesBestCount(nbTimesBestCount);
			}
			else if(instruction == "STATUS") {
				messageToServer.setBestCount(bestCount);
				messageToServer.setBestCountTime(bestCountTime);
				messageToServer.setGraph(graph);
				messageToServer.setStrategy(strategy);
				messageToServer.setNbTimesBestCount(nbTimesBestCount);
			}
			bytesToSend = messageToServer.serialize();
			DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
					bytesToSend.length, IPAddress, 9876);
			clientSocket.send(sendPacket);
			
			// receive response
			receivePacket = new DatagramPacket(receivedBytes,
					receivedBytes.length);
			receivedBytes = receivePacket.getData();
			messageFromServer = Message.deserialize(receivedBytes);	
			Client.currentMsgFromServer = messageFromServer;
			System.out.println("From Server: " + messageFromServer.getMessage());
			
			clientSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
