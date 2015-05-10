package edu.ucsb.cs290cloud.client;

import edu.ucsb.cs290cloud.strategies.Strategy1;

public class Main {

	public final static int PORT_DEFAULT = 50000;
	public final static String IP_DEFAULT = "localhost";
	
	// start a client here
	public static void main(String[] args) {
		Client client;		
		client = new Client(PORT_DEFAULT, IP_DEFAULT, Strategy1.class);
		client.run();
	}
}

