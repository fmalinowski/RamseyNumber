package edu.ucsb.cs290cloud.strategies;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;

public abstract class Strategy extends Thread {
	
	public enum Status {
		COUNTER_EXAMPLE,
		BEING_COMPUTED,
		NOT_YET_STARTED
	}
	
	private volatile Status strategyStatus;
	private volatile GraphWithInfos currentGraph;
	
	public abstract void runStrategy();
	
	public void run() {
		this.runStrategy();
	}

	public void setInitialGraph(GraphWithInfos graph) {
		this.setStrategyStatus(Status.BEING_COMPUTED, graph);
	}
	
	protected GraphWithInfos getInitialGraph() {
		return this.currentGraph.clone();
	}
	
	protected synchronized void setStrategyStatus(Status status, GraphWithInfos graph) {
		this.currentGraph = graph;
		this.strategyStatus = status;
	}
	
	public synchronized Status getStrategyStatus() {
		if (this.strategyStatus == null) {
			return Status.NOT_YET_STARTED;
		}
		return this.strategyStatus;
	}
	
	public synchronized GraphWithInfos getGraph() {
		if (this.currentGraph != null) {
			return this.currentGraph.clone();
		}
		return null;
	}
	
	public void resetStrategy() {
		this.strategyStatus = Status.NOT_YET_STARTED;
		this.currentGraph = null;
	}
}
