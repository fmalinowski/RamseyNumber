package edu.ucsb.cs290cloud.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public final static int SERVER_PORT_DEFAULT = 50000;
	
	/**
	 * Starts the server on a master node
	 * @param args
	 */
	public static void main(String[] args) {
		Options options;
		CommandLineParser parser;
		CommandLine cmd;
		
		GraphsExplorer graphsExplorer;
		Scheduler scheduler;
		Server server;
		
		graphsExplorer = new GraphsExplorer();
		scheduler = new Scheduler(graphsExplorer);
		server = new Server(SERVER_PORT_DEFAULT, scheduler);
		
		// Create the command line parser
		parser = new DefaultParser();
		
		// Create the options
		options = new Options();
		options.addOption("l", "load", true, "How to load the counter examples. --load=remote being graphs stored remotely. --load=disk being graphs stored on disk");
		
		cmd = null;
		// Parse the command line arguments
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (cmd.hasOption("l") && cmd.getOptionValue("l").equals("disk")) {
			graphsExplorer.loadGraphsFromDisk();
		}
		else {
			graphsExplorer.loadGraphsFromRemoteService();
		}
		
		server.run();
	}
}
