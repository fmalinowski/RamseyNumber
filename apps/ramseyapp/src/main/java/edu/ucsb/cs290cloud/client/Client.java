package edu.ucsb.cs290cloud.client;

import java.io.*;
import java.net.*;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.networkcommunication.Message;

public class Client implements Runnable {
	
	public static Message currentMsgFromServer;
	private GraphWithInfos currentGraph;

	public void run() {
		
		try {
			new Thread(new ClientMessenger("READY")).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while(true) {
			// Graph things
			if(currentMsgFromServer.getMessage() == "ASDF") {
				;
			}
		}
	}
}
