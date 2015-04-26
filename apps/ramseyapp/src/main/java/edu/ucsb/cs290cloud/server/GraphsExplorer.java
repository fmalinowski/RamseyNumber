package edu.ucsb.cs290cloud.server;

import java.util.ArrayList;
import java.util.LinkedList;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;

public class GraphsExplorer {

	private ArrayList<LinkedList> counterExamples;
	private ArrayList<LinkedList> graphsBeingComputed;
	
	public GraphsExplorer() {
		this.counterExamples = new ArrayList<LinkedList>();
		this.graphsBeingComputed = new ArrayList<LinkedList>();
	}
	
	/**
	 * Add a graph that is under the status COUNTER_EXAMPLE_FOUND_BUT_NOT_SAVED
	 * into the list of counter examples and remove it from the list of graphs
	 * being computed. The method also changes the status of the graph into
	 * COUNTER_EXAMPLE_FOUND_SAVED
	 * @param graph
	 */
	public void addNewCounterExample(GraphWithInfos graph) {
		LinkedList<GraphWithInfos> listCounterExamples;
		LinkedList<GraphWithInfos> listGraphs = this.graphsBeingComputed.get(graph.size());
		GraphWithInfos graphSavedOnServer = null;
		
		for (GraphWithInfos recordedGraph : listGraphs) {
			if (recordedGraph.getGraphID() == graph.getGraphID()) {
				graphSavedOnServer = recordedGraph;
				break;
			}
		}
		if (graphSavedOnServer != null) {
			listGraphs.remove(graphSavedOnServer);
		}
		
		listCounterExamples = this.counterExamples.get(graph.size());
		if (listCounterExamples == null) {
			listCounterExamples = new LinkedList<GraphWithInfos>();
			this.counterExamples.add(graph.size(), listCounterExamples);
		}
		
		graph.setStatus(GraphWithInfos.Status.COUNTER_EXAMPLE_SAVED);
		
		listCounterExamples.add(graph);
	}
	
	/**
	 * Add the graph into the list of the graphs that are computed on the clients.
	 * It also set the status of the graph to BEING_COMPUTED
	 * @param graph
	 */
	public void addGraphBeingComputed(GraphWithInfos graph) {
		LinkedList<GraphWithInfos> listGraphsBeingComputed;
		
		listGraphsBeingComputed = this.graphsBeingComputed.get(graph.size());
		if (listGraphsBeingComputed == null) {
			listGraphsBeingComputed = new LinkedList<GraphWithInfos>();
			this.counterExamples.add(graph.size(), listGraphsBeingComputed);
		}
		
		graph.setStatus(GraphWithInfos.Status.BEING_COMPUTED);
		
		listGraphsBeingComputed.add(graph);
	}
	
	public int getMaxCounterExamplesSize() {
		return this.counterExamples.size();
	}
	
	public int getMaxGraphBeingComputedSize() {
		return -1;
	}
	
	public LinkedList<GraphWithInfos> getCounterExamples(int graphSize) {
		return null;
	}
	
	public GraphWithInfos getGraphBeingComputedWithLowestBestCount(int graphSize) {
		return null;
	}
	
	public LinkedList<GraphWithInfos> getBestCountGraphsOfSize(int graphSize) {
		return null;
	}
	
}
