package edu.ucsb.cs290cloud.commons;

import java.io.Serializable;
import java.util.Random;

import edu.ucsb.cs290cloud.standalone.Graph;

public class GraphWithInfos extends Graph {
	
	private static final long serialVersionUID = -4751788528533102281L;

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
	private String strategyUsed;

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
	
	public String getStrategyUsed() {
		return strategyUsed;
	}

	public void setStrategyUsed(String strategyUsed) {
		this.strategyUsed = strategyUsed;
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
	
	public GraphWithInfos copyGraph(int newGraphSize) {
		GraphWithInfos newGraph;
		
		if (newGraphSize < this.size) {
			System.err.println("ERROR: the new graph must be at least the same size as teh current graph.");
			return null;
		}
		
		newGraph = new GraphWithInfos(newGraphSize);
		
		Random rn = new Random();
		for (int i = 0; i < this.size; i++) {
			
			for (int j = 0; j < this.size; j++) {
				newGraph.setValue(i, j, this.graph[i][j]);
			}

			int s = rn.nextInt(2);
			newGraph.setValue(i, this.size(), s);
		}

		return newGraph;
	}
}
