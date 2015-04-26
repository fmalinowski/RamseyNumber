package edu.ucsb.cs290cloud.client;

import java.io.*;
import java.net.*;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.commons.Message;

public class Client implements Runnable {
	
	public static Message currentMsgFromServer;
	public static Message msgToServer;
	private boolean hasCounterE = false;
	private GraphWithInfos currentGraph;
	private DatagramSocket clientSocket;
	private int port = 9876;

	public void run() {
		
		try {
			InetAddress IPAddress = InetAddress.getByName("localhost");
			clientSocket = new DatagramSocket();			
			msgToServer.setMessage("READY");
			new Thread(new ClientMessenger(msgToServer, clientSocket, IPAddress, port)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while(true) {
			// read server response and then process
			if(currentMsgFromServer.getMessage() == "NEWGRAPH") {
//				currentMsgFromServer.getBestCount();
//				currentMsgFromServer.getBestCountTime();
//				currentMsgFromServer.getGraph();
//				currentMsgFromServer.getNbTimesBestCount();
//				currentMsgFromServer.getStrategy();
				if(hasCounterE) {
					msgToServer.setMessage("COUNTEREXAMPLE");
					hasCounterE = false;
				}
				else msgToServer.setMessage("STATUS");
			}	
			else if(currentMsgFromServer.getMessage() == "CONTINUE") {
//				msgToServer.setBestCount();
//				msgToServer.setBestCountTime();
//				msgToServer.setGraph(graph);
//				msgToServer.setStrategy(strategy);
//				msgToServer.setNbTimesBestCount(nbTimesBestCount);
				
				if(hasCounterE) {
					msgToServer.setMessage("COUNTEREXAMPLE");
					hasCounterE = false;
				}
				else msgToServer.setMessage("STATUS");
			}
		}
	}
}
