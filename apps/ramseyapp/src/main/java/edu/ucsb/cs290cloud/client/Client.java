package edu.ucsb.cs290cloud.client;

import java.io.*;
import java.net.*;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.commons.Message;
import edu.ucsb.cs290cloud.standalone.CounterExamplesFinder;

public class Client implements Runnable {

	private InetAddress serverIPAddress;
	private int port;
	private String host;
	private DatagramSocket clientSocket;
	private DatagramPacket receivePacket;
	private Message messageToServer, messageFromServer;
	private byte[] receivedBytes, bytesToSend;
	private Thread CEFT;
	private ClientCounterEx CEF = new ClientCounterEx();

	public Client(int inPort, String inHost) {
		port = inPort;
		host = inHost;
	}

	public void run() {

		try {
			serverIPAddress = InetAddress.getByName(host);
			clientSocket = new DatagramSocket();
			sendMessage("READY", null);
			Thread CEFT = new Thread(CEF);
			CEFT.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				String status = CEF.getStatus();
				GraphWithInfos graph = CEF.getGraph();

				if (graph != null) {
					sendMessage(status, graph);
					receiveMessage();
					if (messageFromServer.getMessage() == "NEWGRAPH") {
						CEFT.stop();
						GraphWithInfos receivedGraph = messageFromServer
								.getGraph();
						CEF.setNewGraph(receivedGraph);
						CEFT = new Thread(CEF);
						CEFT.start();
					}
				}
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sendMessage(String status, GraphWithInfos graph) {

		try {
			messageToServer = new Message();
			messageToServer.setMessage(status);
			messageToServer.setGraph(graph);
			bytesToSend = messageToServer.serialize();
			DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
					bytesToSend.length, serverIPAddress, port);
			clientSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void receiveMessage() {
		try {
			receivedBytes = new byte[65507];
			receivePacket = new DatagramPacket(receivedBytes,
					receivedBytes.length);
			clientSocket.receive(receivePacket);
			receivedBytes = receivePacket.getData();
			messageFromServer = Message.deserialize(receivedBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
