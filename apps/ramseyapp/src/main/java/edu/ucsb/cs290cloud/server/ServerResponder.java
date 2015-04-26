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

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.commons.Message;

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
		
		messageForClient = this.getNewTaskForClient(messageFromClient);
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
	
	public Message getNewTaskForClient(Message messageFromClient) {
		GraphWithInfos graphForClient, graphFromClient;
		Message answerForClient;
		
		// The incoming message to the client can be:
		// READY to ask a first task to the master node
		// COUNTEREXAMPLE to give a counter example just found by the client
		// STATUS to send an update to master about the current computation on the client machine
		
		// The answer to the client can be:
		// NEWGRAPH in order to switch/start computing on a new graph
		// CONTINUE in order to continue
		
		graphForClient = null;
		answerForClient = new Message();
		
		graphFromClient = messageFromClient.getGraph();
		
		if (messageFromClient.getMessage().equals("READY")) {
			graphForClient = this.scheduler.getNewTask();
			answerForClient.setMessage("NEWGRAPH");
		}
		else if (messageFromClient.getMessage().equals("COUNTEREXAMPLE")) {
			graphForClient = this.scheduler.processFoundCounterExample(graphFromClient);
			answerForClient.setMessage("NEWGRAPH");
		}
		else if(messageFromClient.getMessage().equals("STATUS")) {
			graphForClient = this.scheduler.processStatusUpdateFromClient(graphFromClient);
			
			if (graphForClient == null) {
				answerForClient.setMessage("CONTINUE");
			}
			else {
				answerForClient.setMessage("NEWGRAPH");
			}
		}
		
		answerForClient.setGraph(graphForClient);
		
		return answerForClient;
	}

}
