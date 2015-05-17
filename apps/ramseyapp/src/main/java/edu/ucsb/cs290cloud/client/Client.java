package edu.ucsb.cs290cloud.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.commons.Message;
import edu.ucsb.cs290cloud.strategies.Strategy;
import edu.ucsb.cs290cloud.strategies.Strategy1Distributed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
	static Logger LOGGER = LoggerFactory.getLogger(Client.class);

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
	
	public void sendMessage(String status, GraphWithInfos graph, Class strategyClass) {
		Message messageToServer;
		DatagramPacket sendPacket;
		byte[] bytesToSend;

		// BEGIN OF DEBUG PRINTS AREA
		LOGGER.info("--------------------");
		LOGGER.info("-> sending " + status);
		if (graph != null) {
			LOGGER.info("Best Count: " + graph.getBestCount());
			LOGGER.info(graph.printGraph());
		}
		LOGGER.info("--------------------");
		// END OF DEBUG PRINTS AREA
		
		messageToServer = new Message();
		
		messageToServer.setMessage(status);
		
		if (graph != null) {
			messageToServer.setGraph(graph.clone());
		}
		
		messageToServer.setStrategyClass(strategyClass);
		
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
		LOGGER.info("--------------------");
		LOGGER.info("receive " + messageFromServer.getMessage());
		if (messageFromServer.getGraph() != null) {
			LOGGER.info("Best Count: " + messageFromServer.getGraph().getBestCount());
			LOGGER.info("Size: " + messageFromServer.getGraph().size());
			LOGGER.info(messageFromServer.getGraph().printGraph());
		}
		LOGGER.info("--------------------");
		// END OF DEBUG PRINTS AREA
		
		return messageFromServer;
	}
	
	public void run() {
		Strategy.Status status;
		GraphWithInfos graph, receivedGraph;
		Message messageFromServer;
		String statusString;
		Class strategyClass;
		HashMap<String, String> strategyParameters;
		
		strategyClass = this.strategyClass;
		
		this.sendMessage("READY", null, strategyClass);
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

			this.sendMessage(statusString, graph, strategyClass);
			messageFromServer = this.receiveMessage();
			if (messageFromServer.getMessage().equals("NEWGRAPH")) {
				this.strategyThread.stop();
				receivedGraph = messageFromServer.getGraph();
				strategyParameters = messageFromServer.getStrategyParameters();

				try {
					if (this.strategyClass.equals(Strategy1Distributed.class)) {
						this.strategyThread = new Strategy1Distributed(strategyParameters);
					}
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
