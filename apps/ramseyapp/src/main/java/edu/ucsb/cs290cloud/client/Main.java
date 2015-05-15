package edu.ucsb.cs290cloud.client;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.ucsb.cs290cloud.strategies.Strategy1;

public class Main {

	public final static int PORT_DEFAULT = 50000;
	public final static String IP_DEFAULT = "localhost";
	
	// start a client here
	public static void main(String[] args) {
		Options options;
		CommandLineParser parser;
		CommandLine cmd;
		String ipMasterNode;
		
		// Create the command line parser
		parser = new DefaultParser();
				
		// Create the options
		options = new Options();
		options.addOption("i", "ip", true, "IP of master node. --ip=128.0.0.1 Default is localhost");
				
		cmd = null;
		// Parse the command line arguments
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ipMasterNode = cmd.hasOption("i") ? cmd.getOptionValue("i") : IP_DEFAULT;
		
		System.out.println("IP server: " + ipMasterNode);
		
		Client client;		
		client = new Client(PORT_DEFAULT, ipMasterNode, Strategy1.class);
		client.run();
	}
}

