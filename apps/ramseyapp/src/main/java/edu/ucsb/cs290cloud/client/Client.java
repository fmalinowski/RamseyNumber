package edu.ucsb.cs290cloud.client;

import java.io.*;
import java.net.*;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.commons.Message;
import edu.ucsb.cs290cloud.standalone.CounterExamplesFinder;

public class Client implements Runnable {

	public static boolean isStale = true;
	public static Message currentMsgFromServer;
	public static Message msgToServer;
	private CounterExamplesFinder counterExFinder;
	private GraphWithInfos currentGraph;
	private DatagramSocket clientSocket;
	private int port;
	private String host;

	public Client(int inPort, String inHost) {
		port = inPort;
		host = inHost;
	}
	
	public void sendCounterEx(CounterExamplesFinder cE) {
		msgToServer.setMessage("COUNTEREXAMPLE");
		msgToServer.setGraph(currentGraph);
		// reset the counterexample flag
		cE.foundCounterEx = false;
	}

	public void run() {

		try {
			counterExFinder = new CounterExamplesFinder();
			InetAddress serverIPAddress = InetAddress.getByName(host);
			clientSocket = new DatagramSocket();
			// initial message
			msgToServer.setMessage("READY");
			new Thread(new ClientMessenger(msgToServer, clientSocket,
					serverIPAddress, port)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			// if new msg is available
			if (!isStale) {
				try {
					// read server response and then process
					if (currentMsgFromServer.getMessage() == "NEWGRAPH") {
						currentGraph = currentMsgFromServer.getGraph();
						// run strategy w/ the graph
						counterExFinder.startStrategy1(currentGraph);

						// if a counter example is found...
						if (counterExFinder.foundCounterEx) {
							sendCounterEx(counterExFinder);
						} else {
							// status message
							msgToServer.setMessage("STATUS");
						}

					} else if (currentMsgFromServer.getMessage() == "CONTINUE") {

						counterExFinder.startStrategy1(currentGraph);
						if (counterExFinder.foundCounterEx) {
							sendCounterEx(counterExFinder);
						} else {
							// status message
							msgToServer.setMessage("STATUS");
						}
					}
					// set stale flag to true
					isStale = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// no new message, so continue working
				counterExFinder.startStrategy1(currentGraph);
				// if a counter example is found...
				if (counterExFinder.foundCounterEx) {
					msgToServer.setMessage("COUNTEREXAMPLE");
					msgToServer.setGraph(currentGraph);
					// reset the counterexample flag
					counterExFinder.foundCounterEx = false;
				} else {
					// status message
					msgToServer.setMessage("STATUS");
				}
			}
		}
	}
}
