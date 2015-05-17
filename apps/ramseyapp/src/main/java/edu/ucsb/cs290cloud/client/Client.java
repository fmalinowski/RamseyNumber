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
	
	public void sendMessage(String status, GraphWithInfos graph) {
		Message messageToServer;
		DatagramPacket sendPacket;
		byte[] bytesToSend;

		// BEGIN OF DEBUG PRINTS AREA
		LOGGER.debug("--------------------");
		LOGGER.debug("-> sending " + status);
		if (graph != null) {
			LOGGER.debug("Best Count: " + graph.getBestCount());
			LOGGER.debug(graph.printGraph());
		}
		LOGGER.debug("--------------------");
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
		LOGGER.debug("--------------------");
		LOGGER.debug("receive " + messageFromServer.getMessage());
		if (messageFromServer.getGraph() != null) {
			LOGGER.debug("Best Count: " + messageFromServer.getGraph().getBestCount());
			LOGGER.debug("Size: " + messageFromServer.getGraph().size());
			LOGGER.debug(messageFromServer.getGraph().printGraph());
		}
		LOGGER.debug("--------------------");
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
