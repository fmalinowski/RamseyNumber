package edu.ucsb.cs290cloud.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.commons.Message;
import edu.ucsb.cs290cloud.strategies.Strategy;
import edu.ucsb.cs290cloud.strategies.Strategy1;

public class Client {
	
	private InetAddress serverIPAddress;
	private int port;
	private String host;
	private DatagramSocket clientSocket;
	private Class strategyClass;
	private Strategy strategyThread;
	
	public Client(int port, String host, Class strategyClass) {
		this.port = port;
		this.host = host;
		this.strategyClass = strategyClass;
		try {
			this.strategyThread = (Strategy) strategyClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.serverIPAddress = InetAddress.getByName(host);
			this.clientSocket = new DatagramSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String status, GraphWithInfos graph) {
		Message messageToServer;
		DatagramPacket sendPacket;
		byte[] bytesToSend;
		
		// BEGIN OF DEBUG PRINTS AREA
		System.out.println("--------------------");
		System.out.println("-> sending " + status);
		if (graph != null) {
			System.out.println("Best Count: " + graph.getBestCount());
			System.out.println(graph.printGraph());
		}
		System.out.println("--------------------");
		// END OF DEBUG PRINTS AREA
		
		messageToServer = new Message();
		
		messageToServer.setMessage(status);
		
		if (graph != null) {
			messageToServer.setGraph(graph.clone());
		}
		
		bytesToSend = messageToServer.serialize();
		
		sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, 
				this.serverIPAddress, this.port);
		
		try {
			this.clientSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Message receiveMessage() {
		DatagramPacket receivePacket;
		Message messageFromServer;
		byte[] receivedBytes;
		
		messageFromServer = null;
		receivedBytes = new byte[65507];
		receivePacket = new DatagramPacket(receivedBytes, receivedBytes.length);
		
		try {
			this.clientSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		receivedBytes = receivePacket.getData();
		messageFromServer = Message.deserialize(receivedBytes);
		
		// BEGIN OF DEBUG PRINTS AREA
		System.out.println("--------------------");
		System.out.println("receive " + messageFromServer.getMessage());
		if (messageFromServer.getGraph() != null) {
			System.out.println("Best Count: " + messageFromServer.getGraph().getBestCount());
			System.out.println("Size: " + messageFromServer.getGraph().size());
			System.out.println(messageFromServer.getGraph().printGraph());
		}
		System.out.println("--------------------");
		// END OF DEBUG PRINTS AREA
		
		return messageFromServer;
	}
	
	public void run() {
		Strategy.Status status;
		GraphWithInfos graph, receivedGraph;
		Message messageFromServer;
		String statusString;
		
		this.sendMessage("READY", null);
		messageFromServer = this.receiveMessage();
		
		this.strategyThread.setInitialGraph(messageFromServer.getGraph());
		this.strategyThread.start();

		while (true) {
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			status = this.strategyThread.getStrategyStatus();
			graph = this.strategyThread.getGraph();
			
			statusString = status == Strategy.Status.COUNTER_EXAMPLE ? "COUNTEREXAMPLE" : "STATUS";

			this.sendMessage(statusString, graph);
			messageFromServer = this.receiveMessage();
			if (messageFromServer.getMessage().equals("NEWGRAPH")) {
				this.strategyThread.stop();
				receivedGraph = messageFromServer.getGraph();

				try {
					this.strategyThread = (Strategy) this.strategyClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				this.strategyThread.setInitialGraph(receivedGraph);
				this.strategyThread.start();
			}
		}
	}
}
