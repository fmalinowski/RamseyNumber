package edu.ucsb.cs290cloud.client;

import java.io.*;
import java.net.*;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.networkcommunication.Message;

public class Client implements Runnable {
	
	public static Message currentMsgFromServer;
	public static Message msgToServer;
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
			// Graph things
			if(currentMsgFromServer.getMessage() == "NEWGRAPH") {
//				msgToServer.setBestCount();
//				msgToServer.setBestCountTime();
//				msgToServer.setGraph(graph);
//				msgToServer.setStrategy(strategy);
//				msgToServer.setNbTimesBestCount(nbTimesBestCount);
			}	
			else if(currentMsgFromServer.getMessage() == "CONTINUE") {

			}
		}
	}
}
