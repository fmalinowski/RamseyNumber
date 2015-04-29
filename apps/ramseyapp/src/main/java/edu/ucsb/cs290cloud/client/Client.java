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
	private boolean hasCounterE = false;
	private GraphWithInfos currentGraph;
	private DatagramSocket clientSocket;
	private int port = 9876;

	public void sendCounterEx() {
		msgToServer.setMessage("COUNTEREXAMPLE");
		msgToServer.setGraph(currentGraph);
		// reset the counterexample flag
		CounterExamplesFinder.foundCounterEx = false;
	}

	public void run() {

		try {
			InetAddress IPAddress = InetAddress.getByName("localhost");
			clientSocket = new DatagramSocket();
			// initial message
			msgToServer.setMessage("READY");
			new Thread(new ClientMessenger(msgToServer, clientSocket,
					IPAddress, port)).start();
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
						new CounterExamplesFinder().startStrategy1(currentGraph);

						// if a counter example is found...
						if (CounterExamplesFinder.foundCounterEx) {
							sendCounterEx();
						} else {
							// status message
							msgToServer.setMessage("STATUS");
						}

					} else if (currentMsgFromServer.getMessage() == "CONTINUE") {

						new CounterExamplesFinder().startStrategy1(currentGraph);
						if (CounterExamplesFinder.foundCounterEx) {
							sendCounterEx();
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
				new CounterExamplesFinder().startStrategy1(currentGraph);
				// if a counter example is found...
				if (CounterExamplesFinder.foundCounterEx) {
					msgToServer.setMessage("COUNTEREXAMPLE");
					msgToServer.setGraph(currentGraph);
					// reset the counterexample flag
					CounterExamplesFinder.foundCounterEx = false;
				} else {
					// status message
					msgToServer.setMessage("STATUS");
				}
			}
		}
	}
}
