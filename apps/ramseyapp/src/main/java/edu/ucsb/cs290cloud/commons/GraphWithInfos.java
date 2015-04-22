package edu.ucsb.cs290cloud.commons;

import edu.ucsb.cs290cloud.standalone.Graph;

public class GraphWithInfos extends Graph {
	
	public enum Status {
		BEING_COMPUTED,
		COUNTER_EXAMPLE_FOUND_BUT_NOT_SAVED,
		COUNTER_EXAMPLE_SAVED
	}
	
	private int parentGraphID;
	private int bestCount;
	private int graphID;
	private Status status; 
	private int timeSpentOnBestCount;

	public GraphWithInfos(int size) {
		super(size);
	}
	
	public GraphWithInfos(int [][] graph) {
		super(graph);
	}
	
	public int getParentGraphID() {
		return parentGraphID;
	}
	
	public void setParentGraphID(int parentGraphID) {
		this.parentGraphID = parentGraphID;
	}

	public int getBestCount() {
		return bestCount;
	}

	public void setBestCount(int bestCount) {
		this.bestCount = bestCount;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public int getTimeSpentOnBestCount() {
		return timeSpentOnBestCount;
	}

	public void setTimeSpentOnBestCount(int timeSpentOnBestCount) {
		this.timeSpentOnBestCount = timeSpentOnBestCount;
	}
	
	public int getGraphID() {
		if (this.status == Status.COUNTER_EXAMPLE_SAVED) {
			return super.getGraphID();
		}
		else {
			return this.graphID;
		}
	}

	public void setGraphID(int graphID) {
		if (this.status != Status.COUNTER_EXAMPLE_SAVED) {
			this.graphID = graphID;
		}
	}
}
