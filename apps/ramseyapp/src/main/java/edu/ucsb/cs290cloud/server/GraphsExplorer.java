package edu.ucsb.cs290cloud.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import edu.ucsb.cs290cloud.commons.GraphFactory;
import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.graphdao.GraphDao;
import edu.ucsb.cs290cloud.graphdao.GraphDaoParse;

import org.slf4j.LoggerFactory;

public class GraphsExplorer {
	static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GraphsExplorer.class);
	public final static int MAX_NUMBER_OF_GRAPHS_STORED_IN_MEMORY_PER_SIZE = 5;

	private HashMap<Integer, LinkedList<GraphWithInfos>> counterExamples;
	private HashMap<Integer, LinkedList<GraphWithInfos>> graphsBeingComputed;
	private GraphsStore graphsStore;
	private GraphDao graphDao;
	
	public GraphsExplorer() {
		this.counterExamples = new HashMap<Integer, LinkedList<GraphWithInfos>>();
		this.graphsBeingComputed = new HashMap<Integer, LinkedList<GraphWithInfos>>();
		this.graphDao = new GraphDaoParse();
		
		try {
			this.graphsStore = new GraphsStore(System.getProperty(("user.dir")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a graph that is under the status COUNTER_EXAMPLE_FOUND_BUT_NOT_SAVED
	 * into the list of counter examples and remove it from the list of graphs
	 * being computed. The method also changes the status of the graph into
	 * COUNTER_EXAMPLE_FOUND_SAVED
	 * @param graph
	 */
	public void addNewCounterExample(GraphWithInfos graph) {
		this.addNewCounterExampleWithStoreOption(graph, true);
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
			this.graphsBeingComputed.put(graph.size(), listGraphsBeingComputed);
		}
		
		graph.setStatus(GraphWithInfos.Status.BEING_COMPUTED);
		
		this.addGraphBeingComputedToListAndKeepBestCounts(listGraphsBeingComputed, graph);
		
		LOGGER.info("----------------------");
		LOGGER.info("GRAPH Being Computed");
		LOGGER.info("Size: " + graph.size());
		LOGGER.info("BestCount: " + graph.getBestCount());
		LOGGER.info(graph.printGraph());
		LOGGER.info("----------------------");
	}
	
	public int getMaxCounterExamplesSize() {
		if (this.counterExamples.size() == 0) {
			return 0;
		}
		return Collections.max(this.counterExamples.keySet());
	}
	
	public int getMaxGraphBeingComputedSize() {
		if (this.graphsBeingComputed.size() == 0) {
			return 0;
		}
		return Collections.max(this.graphsBeingComputed.keySet());
	}
	
	public LinkedList<GraphWithInfos> getCounterExamples(int graphSize) {
		return this.counterExamples.get(graphSize);
	}
	
	public GraphWithInfos getGraphBeingComputedWithLowestBestCount(int graphSize) {
		LinkedList<GraphWithInfos> listGraphs;
		
		listGraphs = this.graphsBeingComputed.get(graphSize);
		
		if (listGraphs != null && listGraphs.size() > 0) {
			return listGraphs.getFirst();
		}
		return null;
	}
	
	public void addCounterExampleToList(LinkedList<GraphWithInfos> graphsList, 
			GraphWithInfos graph) {
		// Should we keep only the 5 first counter examples found or all of them?
		// Maybe we can just save the 5 first found and store in files all of them
		if (graphsList.size() < MAX_NUMBER_OF_GRAPHS_STORED_IN_MEMORY_PER_SIZE) {
			graphsList.add(graph);
		}
	}
	
	public void addGraphBeingComputedToListAndKeepBestCounts(LinkedList<GraphWithInfos> graphsList, 
			GraphWithInfos graph) {
		int index = 0;
		
		for (index = 0; index < graphsList.size(); index++) {
			// We add at the end of the list even if best count is same
			if (graphsList.get(index).getBestCount() >= graph.getBestCount()) {
				break;
			}
		}
		graphsList.add(index, graph);
		
		if (graphsList.size() > MAX_NUMBER_OF_GRAPHS_STORED_IN_MEMORY_PER_SIZE) {
			graphsList.removeLast();
		}
	}	
	
	public void loadGraphsFromDisk() {
		this.counterExamples = this.graphsStore.loadCounterExamples();
	}
	
	public void loadGraphsFromRemoteService() {
		GraphWithInfos bestCounterExampleStored;
		
		bestCounterExampleStored = this.graphDao.getLatestGraph();
		bestCounterExampleStored.setBestCount(0);
		bestCounterExampleStored.setStatus(GraphWithInfos.Status.COUNTER_EXAMPLE_SAVED);
		
		if (bestCounterExampleStored.isCounterExample()) {
			this.addNewCounterExampleWithStoreOption(bestCounterExampleStored, false);
		}
	}
	
	public void generateInitialGraph(int size) {
		GraphWithInfos randomGraph;
		
		randomGraph = GraphFactory.generateRandomGraph(size);
		// It's definitely not a counter example but we don't store it on remote service or on disk
		this.addNewCounterExampleWithStoreOption(randomGraph, false);
	}
	
	private void addNewCounterExampleWithStoreOption(GraphWithInfos graph, Boolean shouldStore) {
		LinkedList<GraphWithInfos> listCounterExamples;
		LinkedList<GraphWithInfos> listGraphs;
		GraphWithInfos graphSavedOnServer;
		
		// We remove all the graphs being computed of that size and set the list to be null
		this.graphsBeingComputed.remove(graph.size());
		
		listCounterExamples = this.counterExamples.get(graph.size());
		if (listCounterExamples == null) {
			listCounterExamples = new LinkedList<GraphWithInfos>();
			this.counterExamples.put(graph.size(), listCounterExamples);
		}
		
		graph.setStatus(GraphWithInfos.Status.COUNTER_EXAMPLE_SAVED);
		
		this.addCounterExampleToList(listCounterExamples, graph);
		
		if (shouldStore) {
			this.graphsStore.storeCounterExample(graph);
			this.graphDao.storeGraph(graph);
		}
		
		// BEGIN OF DEBUG PRINTS AREA
		LOGGER.info("----------------------");
		LOGGER.info("GOT A COUNTER EXAMPLE!");
		LOGGER.info("Size: " + graph.size());
		LOGGER.info(graph.printGraph());
		LOGGER.info("----------------------");
		// END OF DEBUG PRINTS AREA
	}
}
