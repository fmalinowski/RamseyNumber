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
	
	protected volatile Thread threadKiller;

    public void start() {
        this.threadKiller = new Thread(this);
        this.threadKiller.start();
    }

    public void kill() {
    	this.threadKiller = null;
    }
    
    public Boolean isThreadAlive() {
    	return this.threadKiller == Thread.currentThread();
    }
	
	public abstract void runStrategy();
	
	public void run() {
		this.runStrategy();
	}

	public void setInitialGraph(GraphWithInfos graph) {
		this.setStrategyStatus(Status.BEING_COMPUTED, graph);
	}
	
	protected synchronized GraphWithInfos getInitialGraph() {
		return this.currentGraph.clone();
	}
	
	protected synchronized void setStrategyStatus(Status status, GraphWithInfos graph) {
		this.currentGraph = graph.clone();
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
