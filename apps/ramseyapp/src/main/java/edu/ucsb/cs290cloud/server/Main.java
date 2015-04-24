package edu.ucsb.cs290cloud.server;

public class Main {

	public final static int SERVER_PORT_DEFAULT = 8001;
	
	/**
	 * Starts the server on a master node
	 * @param args
	 */
	public static void main(String[] args) {
		//TODO The code here is just to indicate what should be done
		Scheduler scheduler;
		Server server;
		
		scheduler = new Scheduler();
		server = new Server(SERVER_PORT_DEFAULT, scheduler);
		server.run();
	}
}
