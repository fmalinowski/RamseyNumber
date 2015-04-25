package edu.ucsb.cs290cloud.server;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;

public class Scheduler {
	
	private GraphsExplorer graphsExplorer;
	
	public Scheduler() {
		this.graphsExplorer = new GraphsExplorer();
	}

	/**
	 * It is called the first time the client connects to master.
	 * @return the first graph that should be computed by the client.
	 */
	public GraphWithInfos getNewTask() {
		// TODO
		// Get graph Max Size of Counter Examples and get graph being computed with lowest
		// best count that is one size bigger than the graph max size of counter example
		// Otherwise if no graph being computed, we generate new graph that is one size bigger
		// than the biggest counter example size
		// if we just start program, we generate empty graph at size X (X=35?)
		return null;
	}
	
	/**
	 * Called when a client has found a counter example
	 * @return the new graph that should be computed by the client
	 */
	public GraphWithInfos processFoundCounterExample(GraphWithInfos graphFromClient) {
		// TODO
		// Store Counter Example
		// Get graph Max Size of Counter Examples and get graph being computed with lowest
		// best count that is one size bigger than the graph max size of counter example
		// Otherwise if no graph being computed, we generate new graph that is one size bigger
		// than the biggest counter example size
		return null;
	}
	
	/**
	 * Called when the client sends a periodical update about its computation
	 * @return null if the client should continue its work on the current matrix otherwise it 
	 * returns the new graph that should be computed by the client
	 */
	public GraphWithInfos processStatusUpdateFromClient(GraphWithInfos graphFromClient) {
		
		// TODO
		// Get graph Max Size of Counter Examples and get graph being computed with lowest
		// best count that is one size bigger than the graph max size of counter example
		return null;
	}
	
}
