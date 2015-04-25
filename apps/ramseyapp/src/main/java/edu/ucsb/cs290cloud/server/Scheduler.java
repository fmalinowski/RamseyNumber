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
	public GraphWithInfos getNewTask(GraphWithInfos graphFromClient) {
		return null;
	}
	
	/**
	 * Called when a client has found a counter example
	 * @return the new graph that should be computed by the client
	 */
	public GraphWithInfos processFoundCounterExample(GraphWithInfos graphFromClient) {
		return null;
	}
	
	/**
	 * Called when the client sends a periodical update about its computation
	 * @return null if the client should continue its work on the current matrix otherwise it 
	 * returns the new graph that should be computed by the client
	 */
	public GraphWithInfos processStatusUpdateFromClient(GraphWithInfos graphFromClient) {
		return null;
	}
	
}
