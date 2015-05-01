package edu.ucsb.cs290cloud.client;

public class Main {

	public final static int PORT_DEFAULT = 8001;
	public final static String IP_DEFAULT = "localhost";
	
	// start a client here
	public static void main(String[] args) {
		Client client;		
		client = new Client(PORT_DEFAULT, IP_DEFAULT);
		client.run();
	}
}

