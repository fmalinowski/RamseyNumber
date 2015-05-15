package edu.ucsb.cs290cloud.client;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.ucsb.cs290cloud.strategies.Strategy1;
import edu.ucsb.cs290cloud.strategies.Strategy1Random;

public class Main {

	public final static int PORT_DEFAULT = 50000;
	public final static String IP_DEFAULT = "localhost";
	
	public final static String DEFAULT_STRATEGY = "strategy1";
	
	// start a client here
	public static void main(String[] args) {
		Options options;
		CommandLineParser parser;
		CommandLine cmd;
		String ipMasterNode;
		String clientStrategy;
		Class strategyClass;
		
		// Create the command line parser
		parser = new DefaultParser();
				
		// Create the options
		options = new Options();
		options.addOption("i", "ip", true, "IP of master node. --ip=128.0.0.1 Default is localhost");
		options.addOption("s", "strategy", true, "Strategy to be used. --strategy=strategy1");
				
		cmd = null;
		// Parse the command line arguments
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ipMasterNode = cmd.hasOption("i") ? cmd.getOptionValue("i") : IP_DEFAULT;
		clientStrategy = cmd.hasOption("s") ? cmd.getOptionValue("s") : DEFAULT_STRATEGY;
		
		strategyClass = getStrategyClass(clientStrategy);
		
		Client client;		
		client = new Client(PORT_DEFAULT, ipMasterNode, strategyClass);
		client.run();
	}
	
	public static Class getStrategyClass(String strategyString) {
		if (strategyString.equalsIgnoreCase("strategy1random")) {
			return Strategy1Random.class;
		}
		else {
			return Strategy1.class;
		}
	}
}

